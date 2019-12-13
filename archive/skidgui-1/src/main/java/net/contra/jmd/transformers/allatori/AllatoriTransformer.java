package net.contra.jmd.transformers.allatori;

import net.contra.jmd.transformers.Transformer;
import net.contra.jmd.util.GenericMethods;
import net.contra.jmd.util.LogHandler;
import net.contra.jmd.util.NonClassEntries;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AllatoriTransformer implements Transformer {
    LogHandler logger = new LogHandler("AllatoriTransformer");
    Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    ClassGen ALLATORI_CLASS;
    String JAR_NAME;
    boolean isStrong = false;

    public AllatoriTransformer(String jarfile, boolean strong) throws Exception {
        logger.log("Allatori Deobfuscator");
        isStrong = strong;
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
                ClassGen cg = new ClassGen(new ClassParser(jf
                        .getInputStream(entry), entry.getName()).parse());
                if (isStringClass(cg) || isStringClassB(cg)) {
                    ALLATORI_CLASS = cg;
                    logger.debug("Allatori Class: " + ALLATORI_CLASS.getClassName());
                }
                cgs.put(cg.getClassName(), cg);
            } else {
                NonClassEntries.add(entry, jf.getInputStream(entry));
            }
        }
        jf.close();
    }

    private boolean isStringClass(ClassGen cg) {
        if (cg.getMethods().length == 2 && cg.getMethods()[0].isStatic()
                && cg.getMethods()[1].isStatic()) {
            if (cg.getMethods()[0].getReturnType().toString().equals(
                    "java.lang.String")
                    && cg.getMethods()[1].getReturnType().toString().equals(
                    "java.lang.String")) {
                return true;
            }
        }
        return false;
    }

    private boolean isStringClassB(ClassGen cg) {
        if (cg.getMethods().length == 1 && cg.getMethods()[0].isStatic()) {
            if (cg.getMethods()[0].getReturnType().toString().equals("java.lang.String")) {
                return true;
            }
        }
        return false;
    }

    public static String decode(String string) {
        int i = 85;
        char[] cs = new char[string.length()];
        int pos = cs.length - 1;
        int index = pos;
        int xor = i;
        while (pos >= 0) {
            char c = (char) (string.charAt(index) ^ xor);
            int c1_index = index;
            xor = (char) ((char) (c1_index ^ xor) & '?');
            cs[c1_index] = c;
            if (--index < 0) {
                break;
            }
            char c2 = (char) (string.charAt(index) ^ xor);
            int c2_index = index;
            xor = (char) ((char) (c2_index ^ xor) & '?');
            cs[c2_index] = c2;
            pos = --index;
        }

        return new String(cs);
    }
   	public static String decodeContext(String encrypted, String callingClass, String callingMethod) {
		String keyString = callingClass + callingMethod;
		int lastKeyIndex = keyString.length() - 1;
		int xor = 85;
		int keyIndex = lastKeyIndex;
		int length = encrypted.length();
		char[] cs = new char[length];
		for (int i = length - 1; i >= 0; i--) {
			if (keyIndex < 0) {
				keyIndex = lastKeyIndex;
			}
			char keyChar = keyString.charAt(keyIndex--);
			cs[i] = (char) (keyChar ^ (encrypted.charAt(i) ^ xor));
			xor = (char) (63 & (xor ^ (i ^ keyChar)));
		}
		return new String(cs);
	}

    public void transform() throws TargetLostException {
        logger.log("Starting Encrypted String Removal...");
        replaceStrings();
        logger.log("Deobfuscation Finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");
    }

    public void replaceStrings() throws TargetLostException {
        for (ClassGen cg : cgs.values()) {
            int replaced = 0;
            for (Method method : cg.getMethods()) {
                MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    continue;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                for (int i = 1; i < handles.length; i++) {
                    if (handles[i].getInstruction() instanceof INVOKESTATIC && handles[i - 1].getInstruction() instanceof LDC) {
                            INVOKESTATIC methodCall = (INVOKESTATIC) handles[i].getInstruction();
                            if (methodCall.getClassName(cg.getConstantPool()).contains(ALLATORI_CLASS.getClassName())) {
                                LDC encryptedLDC = (LDC) handles[i - 1].getInstruction();
                                String encryptedString = encryptedLDC.getValue(cg.getConstantPool()).toString();
                                String decryptedString;
                                if(isStrong){
                                    decryptedString = decodeContext(encryptedString, cg.getClassName(), method.getName());
                                } else {
                                    decryptedString = decode(encryptedString);
                                }
                                logger.debug(encryptedString + " -> " + decryptedString + " in " + cg.getClassName() + "." + method.getName());
                                int stringRef = cg.getConstantPool().addString(decryptedString);
                                LDC lc = new LDC(stringRef);
                                NOP nop = new NOP();
                                handles[i].setInstruction(lc);
                                handles[i - 1].setInstruction(nop);
                                replaced++;
                            }
                    }
                }
                mg.setInstructionList(list);
                mg.setMaxLocals();
                mg.setMaxStack();
                cg.replaceMethod(method, mg.getMethod());
            }
            if (replaced > 0) {
                logger.debug("decrypted " + replaced + " strings in class " + cg.getClassName());
            }
        }
    }
}
