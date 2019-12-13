package net.contra.jmd.transformers.dasho;

import net.contra.jmd.transformers.Transformer;
import net.contra.jmd.util.GenericMethods;
import net.contra.jmd.util.LogHandler;
import net.contra.jmd.util.NonClassEntries;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

public class DashOTransformer implements Transformer {
    private static LogHandler logger = new LogHandler("DashOTransformer");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    String JAR_NAME;
    String decryptor = "NOTFOUND";
    String decryptorclass = "NOTFOUND";

    public DashOTransformer(String jarfile) throws Exception {
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
                cgs.put(cg.getClassName(), cg);
            } else {
                NonClassEntries.add(entry, jf.getInputStream(entry));
            }
        }
        jf.close();
    }

    public static String decrypt(String input) {
        if (isEmpty(input)) {
            return input;
        }

        char[] inputChars = input.toCharArray();

        int length = inputChars.length;
        char[] inputCharsCopy = new char[length];
        int j = 0;
        int i = 0;

        while (j < length) {
            inputCharsCopy[j] = ((char) (inputChars[j] - '\1' ^ i));

            i = (char) (i + 1);
            j++;
        }

        return new String(inputCharsCopy);
    }

    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public void setDecryptor() {
        for (ClassGen cg : cgs.values()) {
            for (Method m : cg.getMethods()) {
                try {
                    if (m.isPublic() && m.isStatic() &&
                            m.getArgumentTypes()[0].toString().equals("java.lang.String")
                            && m.getReturnType().toString().equals("java.lang.String")) {
                        String dc = cg.getClassName() + "." + m.getName();
                        decryptor = m.getName();
                        decryptorclass = cg.getClassName();
                        logger.debug("Found String Decryptor! " + dc);
                        return;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        logger.error("String decrypt not found!");
    }

    public void removeStringEncryption() {
        for (ClassGen cg : cgs.values()) {
            for (Method m : cg.getMethods()) {
                MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                InstructionList il = mg.getInstructionList();
                InstructionHandle[] handles = il.getInstructionHandles();
                for (int i = 1; i < handles.length; i++) {
                    if ((handles[i].getInstruction() instanceof LDC)
                            && (handles[i + 1].getInstruction() instanceof INVOKESTATIC)) {
                        INVOKESTATIC invoke = (INVOKESTATIC) handles[i + 1].getInstruction();
                        if (decryptor.equals("NOTFOUND")) {
                            logger.error("String Decryption Method not Set!");
                            return;
                        }
                        String call = invoke.getClassName(cg.getConstantPool());
                        String mcall = invoke.getMethodName(cg.getConstantPool());
                        if (call.equals(decryptorclass) && mcall.equals(decryptor)) {
                            LDC orig = ((LDC) handles[i].getInstruction());
                            String enc = (String) orig.getValue(cg.getConstantPool());
                            int index = cg.getConstantPool().addString(decrypt(enc));
                            LDC lc = new LDC(index);
                            handles[i].setInstruction(lc);
                            handles[i + 1].setInstruction(new NOP());
                            logger.debug(enc + " -> " + decrypt(enc));
                        }
                    }
                }
                mg.setInstructionList(il);
                mg.setMaxLocals();
                mg.setMaxStack();
                cg.replaceMethod(m, mg.getMethod());
            }
        }
    }

    public void dumpJar(String path) {
        FileOutputStream os;
        try {
            os = new FileOutputStream(new File(path));
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("could not create file \"" + path + "\": " + fnfe);
        }
        JarOutputStream jos;

        try {
            jos = new JarOutputStream(os);
            for (JarEntry jbe : NonClassEntries.entries) {
                JarEntry destEntry = new JarEntry(jbe.getName());
                byte[] bite = IOUtils.toByteArray(NonClassEntries.ins.get(jbe));
                jos.putNextEntry(destEntry);
                jos.write(bite);
                jos.closeEntry();
            }

            for (ClassGen classIt : cgs.values()) {
                jos.putNextEntry(new JarEntry(classIt.getClassName().replace('.', '/') + ".class"));
                jos.write(classIt.getJavaClass().getBytes());
                jos.closeEntry();
                jos.flush();
            }

            jos.closeEntry();
            jos.close();
        } catch (IOException ioe) {
        }
    }

    public void transform() {
        logger.log("DashO Deobfuscator");
        logger.log("Finding String Decryption Method...");
        setDecryptor();
        logger.log("Starting String Encryption Removal...");
        removeStringEncryption();
        //logger.log("Starting Unconditional Branch Remover...");
        //removeControlFlow();
        //unconditionalBranchTransformer();
        //logger.log("Starting Exit Flow Corrector...");
        //exitFlowTransformer();
        logger.log("Deobfuscation finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");

    }
}
