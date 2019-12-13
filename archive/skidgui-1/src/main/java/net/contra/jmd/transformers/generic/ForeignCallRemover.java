package net.contra.jmd.transformers.generic;

import net.contra.jmd.util.GenericMethods;
import net.contra.jmd.util.LogHandler;
import net.contra.jmd.util.NonClassEntries;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ForeignCallRemover {
    private static LogHandler logger = new LogHandler("ForeignCallRemover");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    String JAR_NAME;
    String AuthClass;

    public ForeignCallRemover(String jarfile) throws Exception {
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
                if (isAuthClass(cg)) {
                    logger.debug("Found auth class! " + cg.getClassName());
                    AuthClass = cg.getClassName();
                }
                cgs.put(cg.getClassName(), cg);
            } else {
                NonClassEntries.add(entry, jf.getInputStream(entry));
            }
        }
        jf.close();
    }

    public boolean isAuthClass(ClassGen cg) {
        if (cg.getMethods().length == 2 && cg.getMethods()[0].getArgumentTypes().length == 1) {
            if (cg.getMethods()[0].getArgumentTypes()[0].getSignature().contains("String")) {
                if (cg.getMethods()[0].getReturnType().toString().contains("boolean")) {
                    return true;
                }
            }
        }
        return false;
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
            for (ClassGen classIt : cgs.values()) {
                jos.putNextEntry(new JarEntry(classIt.getClassName().replace('.', '/') + ".class"));
                jos.write(classIt.getJavaClass().getBytes());
                jos.closeEntry();
                jos.flush();
            }
            for (JarEntry jbe : NonClassEntries.entries) {
                if (!jbe.isDirectory()) {
                    JarEntry destEntry = new JarEntry(jbe.getName());
                    byte[] bite = IOUtils.toByteArray(NonClassEntries.ins.get(jbe));
                    jos.putNextEntry(destEntry);
                    jos.write(bite);
                    jos.closeEntry();
                }
            }
            jos.closeEntry();
            jos.close();
        } catch (IOException ioe) {
        }
    }

    public void RemoveCalls() {
        for (ClassGen cg : cgs.values()) {
            int replaced = 0;
            for (Method method : cg.getMethods()) {
                //logger.debug("in method " + method.getName());
                MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    continue;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                for (int i = 0; i < handles.length; i++) {
                    if (GenericMethods.isCall(handles[i].getInstruction())) {
                        String callClass = GenericMethods.getCallClassName(handles[i].getInstruction(), cg.getConstantPool());
                        String callMethod = GenericMethods.getCallMethodName(handles[i].getInstruction(), cg.getConstantPool());
                        if (!callClass.startsWith("java") &&
                                (!callClass.startsWith("org") || callClass.contains("PingBack"))
                                && !cgs.containsKey(callClass)) {
                            if (GenericMethods.getCallArgTypes(handles[i].getInstruction(), cg.getConstantPool()).length == 0) {
                                handles[i].setInstruction(new NOP());
                                if (handles[i + 1].getInstruction() instanceof ASTORE) {
                                    handles[i + 1].setInstruction(new NOP());
                                }
                                logger.debug(callClass + "." + callMethod + " invoke had no arguments, so we NOP");
                                replaced++;
                            } else {
                                //TODO: WRITE SOMETHING TO DETECT MULTIPLE ARGUMENTS
                                handles[i - 1].setInstruction(new NOP());
                                handles[i].setInstruction(new NOP());
                                if (handles[i + 1].getInstruction() instanceof ASTORE) {
                                    handles[i + 1].setInstruction(new NOP());
                                }
                                logger.debug(callClass + "." + callMethod + " invoke had arguments, so we NOP it and the line before");
                                replaced++;
                            }
                            mg.setInstructionList(list);
                            mg.removeNOPs();
                            mg.setMaxLocals();
                            mg.setMaxStack();
                            cg.replaceMethod(method, mg.getMethod());
                        }
                    }

                    if (handles[i].getInstruction() instanceof NEW) {
                        String callClass = ((NEW) handles[i].getInstruction()).getLoadClassType(cg.getConstantPool()).getClassName();
                        //logger.debug(callClass);
                        if (!callClass.startsWith("java") &&
                                (!callClass.startsWith("org") || callClass.contains("PingBack"))
                                && !cgs.containsKey(callClass)) {
                            handles[i].setInstruction(new NOP());
                            logger.debug("NOPed out NEW " + callClass);
                            replaced++;
                        }
                        mg.setInstructionList(list);
                        mg.removeNOPs();
                        mg.setMaxLocals();
                        mg.setMaxStack();
                        cg.replaceMethod(method, mg.getMethod());
                    }
                }
            }
            if (replaced > 0) {
                logger.debug("Removed " + replaced + " foreign calls in " + cg.getClassName());
            }
        }
    }

    public void replaceCheckMethod() {
        ClassGen cg = cgs.get(AuthClass);
        Method method = cg.getMethods()[0];
        MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());

        InstructionList list = new InstructionList();
        list.append(new ICONST(1));
        list.append(new IRETURN());

        mg.removeExceptionHandlers();
        mg.removeLineNumbers();
        mg.removeLocalVariables();
        mg.removeExceptions();
        mg.setInstructionList(list);
        mg.setMaxLocals();
        mg.setMaxStack();

        cg.replaceMethod(method, mg.getMethod());
    }

    public void fixPOPs() {
        for (ClassGen cg : cgs.values()) {
            int replaced = 0;
            for (Method method : cg.getMethods()) {
                //logger.debug("in method " + method.getName());
                MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    continue;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                if (handles[0].getInstruction() instanceof DUP || handles[0].getInstruction() instanceof ASTORE
                        || handles[0].getInstruction() instanceof POP) {
                    handles[0].setInstruction(new NOP());
                    replaced++;
                }
                mg.setInstructionList(list);
                mg.removeNOPs();
                mg.setMaxLocals();
                mg.setMaxStack();
                cg.replaceMethod(method, mg.getMethod());
            }
            if (replaced > 0) {
                logger.debug("Removed " + replaced + " invalid POPs in " + cg.getClassName());
            }
        }
    }

    public void transform() {
        //logger.log("Removing Exception Handlers...");
        //removeExceptions();
        logger.log("Removing Foreign Calls...");
        RemoveCalls();
        logger.log("Fixing DUPs...");
        fixPOPs();
        if (AuthClass != null) {
            logger.log("Replacing Authentication System...");
            replaceCheckMethod();
        } else {
            logger.error("Auth class wasn't found so we couldn't remove it!");
        }
        logger.log("Deobfuscation finished! Dumping jar...");
        dumpJar(JAR_NAME.replace(".jar", "") + "-deob.jar");
        logger.log("Operation Completed.");

    }
}
