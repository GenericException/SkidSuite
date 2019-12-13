package net.contra.jmd.transformers.zkm;

import me.genericskid.util.signatures.SignatureRules;
import net.contra.jmd.transformers.Transformer;
import net.contra.jmd.util.GenericMethods;
import net.contra.jmd.util.LogHandler;
import net.contra.jmd.util.NonClassEntries;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.*;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ZKMTransformer implements Transformer {
    private static LogHandler logger = new LogHandler("ZKMTransformer");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    private Map<String, ArrayList<String>> zkStrings = new HashMap<String, ArrayList<String>>();
    String JAR_NAME;
    private List<Field> flowObstructors = new LinkedList<Field>();
    private Field controlField = null;
    private String controlClass = "";
    private final SignatureRules rules;

    public String getZKMString(String className, int index) {
        return zkStrings.get(className).get(index);
    }

    public ZKMTransformer(String jarfile, SignatureRules rules) throws Exception {
        File jar = new File(jarfile);
        this.rules = rules;
        JAR_NAME = jarfile;
        JarFile jf = new JarFile(jar);
        Enumeration<JarEntry> entries = jf.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry == null) {
                break;
            }
            if (entry.getName().endsWith(".class")) {
                ClassGen cg = new ClassGen(new ClassParser(jf.getInputStream(entry), entry.getName()).parse());
                String className = entry.getName().replace(".class", "").replace("\\", "/").replace("/", ".");
                cgs.put(className, cg);
            } else {
                NonClassEntries.add(entry, jf.getInputStream(entry));
            }
        }
        logger.debug("Classes loaded from JAR");
        jf.close();
    }

    public static boolean typeA(ClassGen cg) {
        for (Method m : cg.getMethods()) {
            if (m.getArgumentTypes().length == 1 && m.getArgumentTypes()[0].equals(Type.getType(char[].class))) {
                return true;
            }
        }
        return false;
    }

    public static char[] findKeyC(ClassGen cg) {
        for (Method m : cg.getMethods()) {
            if (m.getName().contains("clinit")) {
                MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                InstructionHandle[] handles = mg.getInstructionList().getInstructionHandles();
                char[] keyAsChars = new char[5];
                int found = 0;
                for (int i = handles.length - 1; i > 0; i--) {
                    if (found < 5) {
                        //TODO: FIX THIS, THE LAST TWO CHARS FOR THE KEY ARE BEING FOUND PROPERLY BUT SAVED TO THE ARRAY WRONG
                        if ((handles[i - 1].getInstruction() instanceof BIPUSH)
                                && ((handles[i].getInstruction() instanceof GOTO && ((GOTO) handles[i].getInstruction()).getTarget().getInstruction() instanceof IXOR)
                                || handles[i].getInstruction() instanceof IXOR)) {
                            keyAsChars[found] = (char) ((BIPUSH) handles[i - 1].getInstruction()).getValue().intValue();
                            logger.debug(found + " found key char: " + (int) keyAsChars[found] + " line: " + i);
                            found++;
                        } else if ((handles[i - 1].getInstruction() instanceof ICONST)
                                && ((handles[i].getInstruction() instanceof GOTO && ((GOTO) handles[i].getInstruction()).getTarget().getInstruction() instanceof IXOR)
                                || handles[i].getInstruction() instanceof IXOR)) {
                            keyAsChars[found] = (char) ((ICONST) handles[i - 1].getInstruction()).getValue().intValue();
                            logger.debug(found + " found key char: " + (int) keyAsChars[found] + " line: " + i);
                            found++;
                        }
                    } else {
                        break;
                    }
                }
                char[] right = new char[5];
                right[0] = keyAsChars[4];
                right[1] = keyAsChars[3];
                right[2] = keyAsChars[2];
                right[3] = keyAsChars[1];
                right[4] = keyAsChars[0];
                for (char c : right) {
                    logger.debug("KeyChar: " + (int) c);
                }
                if (keyAsChars == new char[5]) {
                    logger.error("ZKM Key Method C Failed, please send in this file to be analyzed.");
                    return null;
                } else {
                    logger.debug("ZKM Key = " + String.valueOf(right));
                    return right;
                }
            }

        }
        return null;
    }

    public static char[] findKeyB(ClassGen cg) {
        return findKeyC(cg);
        /*
		for(Method m : cg.getMethods()) {
			if(m.getName().contains("clinit")) {
				MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
				InstructionHandle[] handles = mg.getInstructionList().getInstructionHandles();
				char[] keyAsChars = new char[5];
				int found = 0;
                int ixor = -1;
                for(int i = handles.length - 1; i < handles.length; i--){
                   if(handles[i].getInstruction() instanceof IXOR){
                       ixor = i;
                       break;
                   }
                }
                if(ixor == -1){
                    logger.error("ZKM Key Method B Failed, attempting method C");
					return findKeyC(cg);
                }
				for(int i = ixor; i > 0; i--) {
					if(found < 5) {
						if((handles[i - 1].getInstruction() instanceof BIPUSH)
								&& (handles[i].getInstruction() instanceof GOTO || handles[i].getInstruction() instanceof IXOR)) {
							keyAsChars[found] = (char) ((BIPUSH) handles[i - 1].getInstruction()).getValue().intValue();
							logger.debug("found key char: " + (char) found);
							found++;
						} else if((handles[i - 1].getInstruction() instanceof ICONST)
								&& (handles[i].getInstruction() instanceof GOTO || handles[i].getInstruction() instanceof IXOR)) {
							keyAsChars[found] = (char) ((ICONST) handles[i - 1].getInstruction()).getValue().intValue();
							found++;
							logger.debug("found key char: " + (char) found);
						}
					} else {
						break;
					}
				}
                char[] right = keyAsChars;
				right[0] = keyAsChars[4];
				right[1] = keyAsChars[3];
				right[2] = keyAsChars[2];
				right[3] = keyAsChars[1];
				right[4] = keyAsChars[0];
				if(keyAsChars == new char[5]) {
					logger.error("ZKM Key Method B Failed, please send in this file to be analyzed.");
					return null;
				} else {
					logger.debug("ZKM Key = " + String.valueOf(right));
					return right;
				}
			}

		}
		return null;       */
    }

    public static char[] findKey(ClassGen cg) {
        if (typeA(cg)) {
            System.out.println("Type A Found!");
            for (Method m : cg.getMethods()) {
                if (m.getArgumentTypes().length == 1 && m.getArgumentTypes()[0].equals(Type.getType(char[].class))) {
                    MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                    InstructionHandle[] handles = mg.getInstructionList().getInstructionHandles();
                    char[] keyAsChars = new char[5];
                    for (InstructionHandle handle : handles) {
                        if (handle.getInstruction() instanceof TABLESWITCH) {
                            TABLESWITCH xor = (TABLESWITCH) handle.getInstruction();
                            for (int a = 0; a < xor.getTargets().length; a++) {
                                Instruction target = xor.getTargets()[a].getInstruction();
                                if (target instanceof BIPUSH) {
                                    keyAsChars[a] = (char) ((BIPUSH) target).getValue().intValue();
                                } else {
                                    keyAsChars[a] = (char) ((ICONST) target).getValue().intValue();
                                }
                            }
                            Instruction target = xor.getTarget().getInstruction();
                            if (target instanceof BIPUSH) {
                                keyAsChars[4] = (char) ((BIPUSH) target).getValue().intValue();
                            } else {
                                keyAsChars[4] = (char) ((ICONST) target).getValue().intValue();
                            }
                        }
                    }
                    return keyAsChars;
                }
            }
        } else {
            for (Method m : cg.getMethods()) {
                if (m.getName().contains("clinit")) {
                    MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                    InstructionHandle[] handles = mg.getInstructionList().getInstructionHandles();
                    char[] keyAsChars;
                    for (InstructionHandle handle : handles) {
                        if (handle.getInstruction() instanceof TABLESWITCH) {
                            TABLESWITCH xor = (TABLESWITCH) handle.getInstruction();
                            keyAsChars = getKeyFromSwitch(xor, cg);
                            if (keyAsChars != null) {
                                return keyAsChars;
                            }
                        }
                    }
                    return findKeyB(cg);
                }
            }
        }

        return null;
    }

    private static char[] getKeyFromSwitch(TABLESWITCH xor, ClassGen cg) {
        char[] keyAsChars = new char[5];
        for (int a = 0; a < xor.getTargets().length; a++) {
            Instruction target = xor.getTargets()[a].getInstruction();
            if (GenericMethods.isNumber(target)) {
                keyAsChars[a] = (char) GenericMethods.getValueOfNumber(target, cg.getConstantPool());
            } else {
                return null;
            }
        }
        Instruction target = xor.getTarget().getInstruction();
        if (target instanceof BIPUSH) {
            keyAsChars[4] = (char) ((BIPUSH) target).getValue().intValue();
        } else if (target instanceof ICONST) {
            keyAsChars[4] = (char) ((ICONST) target).getValue().intValue();
        } else {
            return null;
        }
        return keyAsChars;
    }

    public static String decrypt(String encrypted, char[] key) {
        char[] plainText = encrypted.toCharArray();
        int plainTextLength = plainText.length;
        int keyLength = key.length;
        //
        // encryption
        char[] cryptoText = new char[plainTextLength];
        for (int i = 0; i < plainTextLength; i++) {
            cryptoText[i] = (char) (plainText[i] ^ key[i % keyLength]);
        }

        // finishing
        return new String(cryptoText);
    }

    public void replaceStrings() throws TargetLostException {
        for (ClassGen cg : cgs.values()) {

            int replaced = 0;
            for (Method method : cg.getMethods()) {
                MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null)
                    continue;
                InstructionHandle[] handles = list.getInstructionHandles();
                //if (!typeA(cg)) {
                for (int i = 0; i < handles.length; i++) {
                    if ((handles[i].getInstruction() instanceof GETSTATIC)
                            && ((handles[i + 1].getInstruction() instanceof BIPUSH)
                            || (handles[i + 1].getInstruction() instanceof SIPUSH)
                            || (handles[i + 1].getInstruction() instanceof ICONST))
                            && (handles[i + 2].getInstruction() instanceof AALOAD)) {
                        int push;
                        if (handles[i + 1].getInstruction() instanceof BIPUSH) {
                            push = ((BIPUSH) handles[i + 1].getInstruction()).getValue().intValue();
                        } else if (handles[i + 1].getInstruction() instanceof SIPUSH) {
                            push = ((SIPUSH) handles[i + 1].getInstruction()).getValue().intValue();
                        } else {
                            push = ((ICONST) handles[i + 1].getInstruction()).getValue().intValue();
                        }

                        String decryptedString = getZKMString(cg.getClassName(), push);
                        int stringRef = cg.getConstantPool().addString(decryptedString);
                        LDC lc = new LDC(stringRef);
                        NOP nop = new NOP();
                        handles[i].setInstruction(lc);
                        handles[i + 1].setInstruction(nop);
                        handles[i + 2].setInstruction(nop);
                        replaced++;
                    }
                }
                mg.setInstructionList(list);
                mg.setMaxLocals();
                mg.setMaxStack();
                cg.replaceMethod(method, mg.getMethod());
                /*
                                    } else {
                                        logger.error("This type of ZKM is not currently supported for string deob!");
                                    }        */
            }
            if (replaced > 0) {
                logger.debug("replaced " + replaced + " calls in class " + cg.getClassName());
            }
        }
    }

    public void removeOriginStrings() {
        for (ClassGen cg : cgs.values()) {
            for (Method method : cg.getMethods()) {
                if (method.getName().contains("clinit")) {
                    MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                    InstructionList list = mg.getInstructionList();
                    InstructionHandle[] handles = list.getInstructionHandles();
                    int startLoc = -1;
                    int endLoc = -1;
                    for (int i = 0; i < handles.length; i++) {
                        if (((handles[i].getInstruction() instanceof BIPUSH) || (handles[i].getInstruction() instanceof SIPUSH) || (handles[i].getInstruction() instanceof ICONST))
                                && (handles[i + 1].getInstruction() instanceof ANEWARRAY)) {
                            ANEWARRAY an = (ANEWARRAY) handles[i + 1].getInstruction();
                            ObjectType ty = an.getLoadClassType(cg.getConstantPool());
                            if (ty == null) continue;
                            String type = ty.toString();
                            if (type.equals("java.lang.String")) {
                                startLoc = i;
                                logger.debug("Start Location for <clinit> removal: " + startLoc);
                            }
                        }
                        if (startLoc >= 0) {
                            if (handles.length > (i + 2)) {
                                if (handles[i].getInstruction() instanceof POP
                                        && handles[i + 1].getInstruction() instanceof SWAP
                                        && handles[i + 2].getInstruction() instanceof TABLESWITCH) {
                                    endLoc = i + 2;
                                    logger.debug("End Location for <clinit> removal: " + endLoc);
                                    break;
                                }
                            }
                        }
                    }
                    if ((startLoc >= 0) && (endLoc >= 0)) {
                        try {
                            list.delete(handles[startLoc], handles[endLoc]);
                        } catch (TargetLostException e) {
                            logger.error("Control flow obfuscation evident. Couldn't clear clinit");
                            //e.printStackTrace();
                            return;
                        }
                        logger.debug("NOPed " + (endLoc - startLoc) + " instructions from <clinit> in " + cg.getClassName());
                        list.setPositions();
                        mg.setInstructionList(list);
                        mg.setMaxLocals();
                        mg.setMaxStack();
                        cg.replaceMethod(method, mg.getMethod());
                    }
                }
            }
        }
    }

    //TODO: It isn't finding the last string sometimes, so shit gets all fucked up and it ends up leaving a call to static{} and extra instructions
    public void getStringsFromZKM() {
        for (ClassGen cg : cgs.values()) {
            if (!this.rules.isInclusive() || allowedPackage(cg.getClassName(), this.rules.getPackages())) {

            }
            char[] key = findKey(cg);
            for (Method method : cg.getMethods()) {
                MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    continue;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                ArrayList<String> all = new ArrayList<String>();
                if (method.getName().contains("clinit")) {
                    for (int i = 0; i < handles.length; i++) {
                        if (handles[i].getInstruction() instanceof LDC) {
                            LDC orig = ((LDC) handles[i].getInstruction());
                            if (!orig.getType(cg.getConstantPool()).getSignature().contains("String")) continue;
                            String enc = orig.getValue(cg.getConstantPool()).toString();
                            String dec = decrypt(enc, key);
                            all.add(dec);
                            logger.debug(cg.getClassName() + " -> " + dec);
                        }
                    }
                    zkStrings.put(cg.getClassName(), all);
                    logger.debug("Decrypted and stored " + all.size() + " strings from " + cg.getClassName());
                }
            }
        }
    }

    private static boolean allowedPackage(final String className, final String[] packages) {
        for (final String packageStr : packages) {
            if (className.contains(packageStr)) {
                return true;
            }
        }
        return false;
    }

    private void locateObstructors() {
        for (ClassGen cg : cgs.values()) {
            for (Method method : cg.getMethods()) {
                if (method.isAbstract() || method.isNative()) {
                    continue;
                }
                MethodGen mGen = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionFinder finder = new InstructionFinder(mGen.getInstructionList());
                Iterator<InstructionHandle[]> matches = finder.search(
                        "GETSTATIC IFEQ ((ILOAD IFEQ ICONST GOTO ICONST)|(IINC ILOAD)) PUTSTATIC"
                );
                if (matches.hasNext()) {
                    InstructionHandle[] match = matches.next();
                    GETSTATIC gstatCtrlField = (GETSTATIC) match[0].getInstruction();
                    controlClass = gstatCtrlField.getName(cg.getConstantPool());
                    String fieldName = gstatCtrlField.getFieldName(cg.getConstantPool());
                    ClassGen ctrlClazz = cgs.get(controlClass);
                    controlField = ctrlClazz.containsField(fieldName);
                }
            }
        }
        if (controlField == null) {
            logger.error("Couldn't Locate Control Field!");
            return;
        }
        flowObstructors.add(controlField);
        logger.debug("control field: " + controlClass + "." + controlField.getName() + " " + controlField.getSignature());
        for (ClassGen cg : cgs.values()) {
            final ConstantPoolGen cpg = cg.getConstantPool();
            for (Method method : cg.getMethods()) {
                if (method.isAbstract() || method.isNative()) {
                    continue;
                }
                MethodGen mGen = new MethodGen(method, cg.getClassName(), cpg);
                InstructionFinder finder = new InstructionFinder(mGen.getInstructionList());
                Iterator<InstructionHandle[]> matches = finder.search(
                        "(GETSTATIC|ILOAD) IFEQ (((ILOAD|GETSTATIC) IFEQ ICONST GOTO ICONST)|(IINC ILOAD)) PUTSTATIC",
                        new InstructionFinder.CodeConstraint() {

                            public boolean checkCode(InstructionHandle[] code) {
                                FieldInstruction ctrlFieldInstr = null;
                                if (code[0].getInstruction() instanceof GETSTATIC) {
                                    ctrlFieldInstr = (FieldInstruction) code[0].getInstruction();
                                } else {
                                    ctrlFieldInstr = (FieldInstruction) code[code.length - 2].getInstruction();
                                }
                                String className = ctrlFieldInstr.getName(cpg);
                                String fieldName = ctrlFieldInstr.getFieldName(cpg);
                                return className.equals(controlClass) && fieldName.equals(controlField.getName());
                            }
                        });
                while (matches.hasNext()) {
                    InstructionHandle[] match = matches.next();
                    Instruction first = match[0].getInstruction();
                    ClassGen ctrlClazz;
                    Field flowObstructor = null;
                    if (first instanceof GETSTATIC) {
                        PUTSTATIC pstatCtrlField = (PUTSTATIC) match[match.length - 2].getInstruction();
                        String className = pstatCtrlField.getName(cg.getConstantPool());
                        String fieldName = pstatCtrlField.getFieldName(cg.getConstantPool());
                        ctrlClazz = cgs.get(className);
                        flowObstructor = ctrlClazz.containsField(fieldName);
                    } else {
                        ILOAD iLoad = (ILOAD) first;
                        int idx = iLoad.getIndex();
                        InstructionHandle iStoreHandle = finder.getInstructionList().getInstructionHandles()[1];
                        ISTORE iStore = (ISTORE) iStoreHandle.getInstruction();
                        assert idx == iStore.getIndex() && idx == mGen.getMaxLocals() - 1 : "expected " + idx
                                + " found " + iStore.getIndex();
                        GETSTATIC gstatCtrlField = (GETSTATIC) iStoreHandle.getPrev().getInstruction();
                        String className = gstatCtrlField.getName(cg.getConstantPool());
                        String fieldName = gstatCtrlField.getFieldName(cg.getConstantPool());
                        ctrlClazz = cgs.get(className);
                        flowObstructor = ctrlClazz.containsField(fieldName);
                    }
                    if (!flowObstructors.contains(flowObstructor)) {
                        logger.debug("flow obstructor: " + ctrlClazz.getClassName() + "." + flowObstructor.getName()
                                + " " + flowObstructor.getSignature());
                        flowObstructors.add(flowObstructor);
                    }
                }
            }
        }
    }

    public void transform() {
        logger.log("ZKM Deobfuscator");
        logger.log("Starting Opaque Predicate Remover...");
        locateObstructors();
        opaqueTransformer();
        logger.log("Starting String Encryption Removal...");
        try {
            logger.log("Starting ZKM String Grabber...");
            getStringsFromZKM();
            logger.log("Starting ZKM String Replacer...");
            replaceStrings();
        } catch (TargetLostException e) {
            e.printStackTrace();
        }
        logger.log("Starting String Origin Removal...");
        removeOriginStrings();
        logger.log("Starting Unconditional Branch Remover...");
        unconditionalBranchTransformer();
        logger.log("Starting Exit Flow Corrector...");
        exitFlowTransformer();
        logger.log("Deobfuscation finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");
    }

    private InstructionHandle findIStore(InstructionHandle start, int idx) {
        InstructionHandle ih = start;
        while (ih != null) {
            if (ih.getInstruction() instanceof ISTORE) {
                if (((ISTORE) ih.getInstruction()).getIndex() == idx) {
                    return ih;
                }
            }
            ih = ih.getNext();
        }
        return null;
    }

    public void unconditionalBranchTransformer() {
        for (ClassGen cg : cgs.values()) {
            for (Method method : cg.getMethods()) {
                final MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                if (method.isAbstract() || method.isNative()) {
                    return;
                }
                final InstructionList list = mg.getInstructionList();
                InstructionFinder finder = new InstructionFinder(list);
                final ConstantPoolGen cpg = cg.getConstantPool();
                int branchesSimplified = 0;
                Iterator<InstructionHandle[]> matches = finder.search("IfInstruction");
                while (matches.hasNext()) {
                    InstructionHandle ifHandle = matches.next()[0];
                    InstructionHandle target = ((BranchHandle) ifHandle).getTarget();
                    if (target.getInstruction() instanceof GOTO) {
                        branchesSimplified++;
                        ((BranchHandle) ifHandle).setTarget(((BranchHandle) target).getTarget());
                    }
                }
                matches = finder.search("GOTO GOTO");
                while (matches.hasNext()) {
                    InstructionHandle[] match = matches.next();
                    try {
                        list.delete(match[0]);
                    } catch (TargetLostException tlex) {
                        for (InstructionHandle target : tlex.getTargets()) {
                            for (InstructionTargeter targeter : target.getTargeters()) {
                                targeter.updateTarget(target, match[1]);
                            }
                        }
                    }
                }
                mg.setInstructionList(list);
                mg.setMaxLocals();
                mg.setMaxStack();
                if (branchesSimplified > 0) {
                    logger.debug("simplified " + branchesSimplified + " unconditional branches");
                    cg.replaceMethod(method, mg.getMethod());
                }
            }
        }
    }

    public void exitFlowTransformer() {
        for (ClassGen cg : cgs.values()) {
            int correct = 0;
            for (Method method : cg.getMethods()) {
                final MethodGen mgen = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                if (!method.isAbstract() && !method.isNative()) {
                    InstructionList list = mgen.getInstructionList();
                    InstructionFinder finder = new InstructionFinder(list);
                    CodeExceptionGen[] exceptionGens = mgen.getExceptionHandlers();
                    for (Iterator<InstructionHandle[]> matches = finder.search(
                            "ASTORE ALOAD (NEW DUP (PushInstruction InvokeInstruction)+ (ALOAD IfInstruction LDC GOTO LDC " +
                                    "INVOKEVIRTUAL)? (PushInstruction InvokeInstruction)* InvokeInstruction+)?");
                         matches.hasNext();) {
                        /* thanks to popcorn89 */
                        InstructionHandle[] match = matches.next();
                        if (!(match[match.length - 1].getInstruction() instanceof ATHROW)) {
                            continue;
                        }
                        InstructionHandle astoreInstr = match[0];
                        InstructionHandle athrowInstr = match[match.length - 1];
                        InstructionHandle toRedirect = athrowInstr.getNext();
                        for (CodeExceptionGen exgen : exceptionGens) {
                            if (exgen.getHandlerPC().equals(astoreInstr)) {
                                mgen.removeExceptionHandler(exgen);
                            }
                        }
                        try {
                            list.delete(astoreInstr, athrowInstr);
                        } catch (TargetLostException tlex) {
                            if (athrowInstr == list.getEnd()) {
                                toRedirect = astoreInstr.getPrev();
                            }
                            if (toRedirect != null) {
                                for (InstructionHandle target : tlex.getTargets()) {
                                    for (InstructionTargeter targeter : target.getTargeters()) {
                                        targeter.updateTarget(target, toRedirect);
                                    }
                                }
                            }
                        }
                    }
                    list.setPositions(true);
                    InstructionHandle lastInstr = list.getEnd();
                    InstructionHandle secondToLastInstr = lastInstr.getPrev();
                    if (secondToLastInstr != null && (lastInstr.getInstruction() instanceof RETURN)
                            && (secondToLastInstr.getInstruction() instanceof RETURN)) {
                        try {
                            list.delete(secondToLastInstr);
                        } catch (TargetLostException tlex) {
                            for (InstructionHandle target : tlex.getTargets()) {
                                for (InstructionTargeter targeter : target.getTargeters()) {
                                    targeter.updateTarget(target, lastInstr);
                                }
                            }
                        }
                    }
                    if (mgen.getMethod() != method) {
                        correct++;
                        logger.debug("corrected exit flow in " + cg.getClassName() + "." + mgen.getName() + mgen.getSignature());
                        mgen.setInstructionList(list);
                        mgen.setMaxLocals();
                        mgen.setMaxStack();
                        cg.replaceMethod(method, mgen.getMethod());
                    }
                }
            }
            logger.debug("Corrected exit flow " + correct + " times in " + cg.getClassName());
        }
    }

    public void opaqueTransformer() {
        for (ClassGen cg : cgs.values()) {
            for (Method method : cg.getMethods()) {
                final MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                if (method.isAbstract() || method.isNative()) {
                    return;
                }
                final InstructionList list = mg.getInstructionList();
                InstructionFinder finder = new InstructionFinder(list);
                final ConstantPoolGen cpg = cg.getConstantPool();
                int stripped = 0;

                Iterator<InstructionHandle[]> matches = finder.search(
                        "(ILOAD|GETSTATIC) ((ISTORE)|(IFNE|IFEQ)) (((IINC ILOAD)|((ILOAD IFEQ)? ICONST GOTO ICONST)) PUTSTATIC)?",
                        code -> {
                            InstructionHandle ih = code[0];
                            if (code.length <= 3) {
                                if (ih.getInstruction() instanceof GETSTATIC) {
                                    GETSTATIC gstat = (GETSTATIC) ih.getInstruction();
                                    return flowObstructors.contains(cgs.get(gstat.getName(cpg))
                                            .containsField(gstat.getFieldName(cpg)));
                                } else {
                                    ILOAD iLoad = (ILOAD) ih.getInstruction();
                                    int idx = iLoad.getIndex();
                                    if (idx < mg.getArgumentTypes().length + (mg.isStatic() ? 0 : 1)) {
                                        return false;
                                    }
                                    InstructionHandle storeHandle = findIStore(list.getStart(), idx);
                                    if (storeHandle == null || !(storeHandle.getPrev().getInstruction() instanceof GETSTATIC)) {
                                        return false;
                                    }
                                    GETSTATIC gstat = (GETSTATIC) storeHandle.getPrev().getInstruction();
                                    return flowObstructors.contains(cgs.get(gstat.getName(cpg))
                                            .containsField(gstat.getFieldName(cpg)));
                                }
                            } else {
                                GETSTATIC gstat = (GETSTATIC) ih.getInstruction();
                                ClassGen cp = cgs.get(gstat.getName(cpg));
                                Field fz = cp.containsField(gstat.getFieldName(cpg));
                                return cp != null && fz != null && controlField != null && controlField.equals(fz);
                            }
                        });

                while (matches.hasNext()) {
                    List<InstructionHandle> toDelete = new LinkedList<InstructionHandle>();
                    InstructionHandle[] match = matches.next();
                    InstructionHandle ih = match[0];
                    InstructionHandle theBranch = match[1];
                    InstructionHandle lastInstr = match[match.length - 2];
                    InstructionHandle toRedirect = lastInstr.getNext();
                    if (toRedirect == null) {
                        break;
                    }
                    if (theBranch.getInstruction() instanceof IFEQ && match.length <= 3) {
                        toDelete.add(ih);
                        theBranch.setInstruction(new GOTO(((BranchHandle) theBranch).getTarget()));
                    } else {
                        try {
                            list.delete(ih, lastInstr);
                        } catch (TargetLostException tlex) {
                            for (InstructionHandle target : tlex.getTargets()) {
                                for (InstructionTargeter targeter : target.getTargeters()) {
                                    logger.debug("redirected " + target + " to " + toRedirect + " in " + cg.getClassName() + "." + mg.getName() + mg.getSignature());
                                    targeter.updateTarget(target, toRedirect);
                                }
                            }
                        }
                    }
                    stripped++;
                    for (InstructionHandle del : toDelete) {
                        try {
                            list.delete(del);
                        } catch (TargetLostException tlex) {
                            for (InstructionHandle target : tlex.getTargets()) {
                                for (InstructionTargeter targeter : target.getTargeters()) {
                                    logger.debug("redirected " + target + " to " + (theBranch.getInstruction() instanceof IFEQ ? lastInstr : toRedirect) + " in " + cg.getClassName() + "." + mg.getName() + mg.getSignature());
                                    targeter.updateTarget(target, theBranch.getInstruction() instanceof IFEQ ?
                                            lastInstr : toRedirect);
                                }
                            }
                        }
                    }
                }
                if (stripped > 0) {
                    logger.debug("stripped " + stripped + " opaque predicates from " + cg.getClassName() + "." + mg.getName() + mg.getSignature());
                    mg.setInstructionList(list);
                    mg.setMaxStack();
                    mg.setMaxLocals();
                    cg.replaceMethod(method, mg.getMethod());
                }
            }
        }
    }
}
