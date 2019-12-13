/*
	TODO: Write a dynamic string decryptor like the one in SAE; Pattern is LDC INVOKESTATIC,                     
	grab the IL from the invokestatic method and put it in a new methodgen and run the LDC through that and replace it with the result
	Please see http://www.java-tips.org/java-se-tips/java.lang.reflect/invoke-method-using-reflection.html

	Also ask somebody how you can trace the stack for stringfixer, foreigncallremover
	AND to get the key, check the method arguments. If it is just string pass the string, otherwise grab the integer above it.
	If there is more leave it be or attempt to grab the values
	
	*/
package net.contra.jmd.transformers.generic;

import net.contra.jmd.util.GenericClassLoader;
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

public class GenericStringDeobfuscator {
    private static LogHandler logger = new LogHandler("GenericStringDeobfuscator");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    String JAR_NAME;

    public GenericStringDeobfuscator(String jarfile) throws Exception {
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

    public void replaceStrings() {
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
                    //java.lang.reflect.Method
                    if (GenericMethods.isCall(handles[i].getInstruction()) && handles[i - 1].getInstruction() instanceof LDC) {

                        String methodCallClass = GenericMethods.getCallClassName(handles[i].getInstruction(), cg.getConstantPool());
                        String methodCallMethod = GenericMethods.getCallMethodName(handles[i].getInstruction(), cg.getConstantPool());
                        String methodCallSig = GenericMethods.getCallSignature(handles[i].getInstruction(), cg.getConstantPool());
                        String methodRetType = GenericMethods.getCallReturnType(handles[i].getInstruction(), cg.getConstantPool());

                        if (GenericMethods.getCallArgTypes(handles[i].getInstruction(), cg.getConstantPool()).length == 1
                                && methodCallClass != null && methodCallMethod != null && methodRetType.contains("String")) {
                            //Begin classloader bullshit
                            GenericClassLoader loader = new GenericClassLoader(GenericStringDeobfuscator.class.getClassLoader());
                            Class cl;
                            ClassGen ourCz = cgs.get(methodCallClass);
                            if (ourCz != null && ourCz.containsMethod(methodCallMethod, methodCallSig) != null) {
                                byte[] bit = ourCz.getJavaClass().getBytes();
                                logger.debug(methodCallClass + " " + methodCallSig);
                                logger.debug(ourCz.getClassName());
                                cl = loader.loadClass(ourCz.getClassName(), bit);
                            } else {
                                continue;
                            }
                            if (cl == null) {
                                continue;
                            }
                            java.lang.reflect.Method mthd;
                            try {
                                mthd = cl.getMethod(methodCallMethod, String.class);
                                mthd.setAccessible(true);
                            } catch (NoSuchMethodException e) {
                                continue;
                            }

                            LDC encryptedLDC = (LDC) handles[i - 1].getInstruction();
                            String encryptedString = encryptedLDC.getValue(cg.getConstantPool()).toString();
                            String decryptedString;
                            try {
                                decryptedString = mthd.invoke(null, encryptedString).toString();
                            } catch (Exception e) {
                                continue;
                            }
                            logger.debug(encryptedString + " -> " + decryptedString + " in " + cg.getClassName() + "." + method.getName());
                            int stringRef = cg.getConstantPool().addString(decryptedString);
                            LDC lc = new LDC(stringRef);
                            NOP nop = new NOP();
                            handles[i].setInstruction(lc);
                            handles[i - 1].setInstruction(nop);
                            replaced++;
                        } else {
                            continue;
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

    public void transform() {
        logger.log("Generic String Deobfuscator");
        logger.log("Still basic (and very buggy)");
        replaceStrings();
        logger.log("Deobfuscation finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");

    }
}
