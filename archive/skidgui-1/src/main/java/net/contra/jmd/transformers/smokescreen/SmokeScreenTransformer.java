package net.contra.jmd.transformers.smokescreen;

import net.contra.jmd.transformers.Transformer;
import net.contra.jmd.util.GenericMethods;
import net.contra.jmd.util.LogHandler;
import net.contra.jmd.util.NonClassEntries;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SmokeScreenTransformer implements Transformer {
    private static LogHandler logger = new LogHandler("SmokeScreenTransformer");
    private Map<String, ClassGen> cgs;
    private Map<String, String> ssStrings = new HashMap<String, String>();
    String JAR_NAME;

    public String getActualString(String className, int i1, int i2) {
        return ssStrings.get(className).substring(i1, i2);
    }

    public void replaceStrings() {
        for (ClassGen cg : cgs.values()) {
            int replaced = 0;
            for (Method m : cg.getMethods()) {
                MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    return;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                for (int x = 0; x < handles.length; x++) {
                    if (x + 3 < handles.length) {
                        if (handles[x].getInstruction() instanceof GETSTATIC
                                && GenericMethods.isNumber(handles[x + 1].getInstruction())
                                && GenericMethods.isNumber(handles[x + 2].getInstruction())
                                && GenericMethods.isCall(handles[x + 3].getInstruction())) {
                            if (GenericMethods.getCallMethodName(handles[x + 3].getInstruction(), cg.getConstantPool()).contains("substring")) {
                                int con1 = GenericMethods.getValueOfNumber(handles[x + 1].getInstruction(), cg.getConstantPool());
                                int con2 = GenericMethods.getValueOfNumber(handles[x + 2].getInstruction(), cg.getConstantPool());
                                NOP nop = new NOP();
                                int stringRef = cg.getConstantPool().addString(getActualString(cg.getClassName(), con1, con2));
                                LDC data = new LDC(stringRef);
                                handles[x].setInstruction(data);
                                handles[x + 1].setInstruction(nop);
                                handles[x + 2].setInstruction(nop);
                                handles[x + 3].setInstruction(nop);
                                replaced++;
                                mg.setInstructionList(list);
                                mg.removeNOPs();
                                mg.setMaxLocals();
                                mg.setMaxStack();
                                cg.replaceMethod(m, mg.getMethod());
                                continue;
                            }
                        }
                    }
                }
            }
            if (replaced > 0) {
                logger.debug("Replaced " + replaced + " calls in class " + cg.getClassName());
            }
        }
    }

    public void grabStrings() {
        for (ClassGen cg : cgs.values()) {
            for (Method m : cg.getMethods()) {
                int key = 0;
                MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    return;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                for (int x = 0; x < handles.length; x++) {
                    if (x + 3 < handles.length) {
                        if (handles[x].getInstruction() instanceof LDC
                                && handles[x + 1].getInstruction() instanceof ASTORE
                                && GenericMethods.isNumber(handles[x + 2].getInstruction())
                                && handles[x + 3].getInstruction() instanceof ISTORE) {
                            key = GenericMethods.getValueOfNumber(handles[x + 2].getInstruction(), cg.getConstantPool());
                            LDC tx = (LDC) handles[x].getInstruction();

                            String encryptedContent = tx.getValue(cg.getConstantPool()).toString();
                            String decryptedContent = decrypt(encryptedContent, key);
                            logger.debug("Found key for " + cg.getClassName() + ": " + key);
                            logger.debug("Strings for class: " + decryptedContent);
                            ssStrings.put(cg.getClassName(), decryptedContent);
                            continue;
                        }
                    }
                }
            }
        }
    }

    public SmokeScreenTransformer(String jarfile) throws Exception {
        cgs = new HashMap<String, ClassGen>();
        File jar = new File(jarfile);
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

    public static String decrypt(String encrypted, int myKey) {
        int key = myKey;
        char[] encChars = encrypted.toCharArray();
        char[] tmpChars = new char[encChars.length];
        for (int j = 0; j < encChars.length; j++) {
            tmpChars = encChars;
            tmpChars[j] = (char) (tmpChars[j] ^ key);
            key = (char) (key + encChars[j] & 0x3E);
        }
        return new String(tmpChars);
    }

    public void transform() {
        logger.log("SmokeScreen Deobfuscator");
        logger.log("Decrypting Strings...");
        grabStrings();
        logger.log("Replacing string calls with LDC");
        replaceStrings();
        logger.log("Starting Exit Flow Corrector...");
        exitFlowTransformer();
        logger.log("Removing Unconditional Branches...");
        unconditionalBranchTransformer();
        logger.log("Deobfuscation finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");

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
                            for (InstructionHandle target : tlex.getTargets()) {
                                for (InstructionTargeter targeter : target.getTargeters()) {
                                    targeter.updateTarget(target, toRedirect);
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
                        //logger.debug("corrected exit flow in " + cg.getClassName() + "." + mgen.getName() + mgen.getSignature());
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
}
