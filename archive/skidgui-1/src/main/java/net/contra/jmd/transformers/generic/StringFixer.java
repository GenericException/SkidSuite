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

public class StringFixer {
    private static LogHandler logger = new LogHandler("StringFixer");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    String JAR_NAME;
    boolean replacing = false;

    public StringFixer(String jarfile) throws Exception {
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

    public void removeBASA() {
        for (ClassGen cg : cgs.values()) {
            for (Method method : cg.getMethods()) {
                String type = "";
                MethodGen mg = new MethodGen(method, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    continue;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                int startLoc = -1;
                int endLoc = -1;
                int arrayLength = -1;
                for (int i = 0; i < handles.length; i++) {
                    if ((handles[i].getInstruction() instanceof NEW)
                            && (handles[i + 1].getInstruction() instanceof DUP)
                            && GenericMethods.isNumber(handles[i + 2].getInstruction())
                            && (handles[i + 3].getInstruction() instanceof NEWARRAY)) {
                        String newType = ((NEW) handles[i].getInstruction()).getLoadClassType(cg.getConstantPool()).toString();
                        type = ((NEWARRAY) handles[i + 3].getInstruction()).getType().toString();
                        logger.debug("Found new array conversion pattern: " + type + "->" + newType + " in " + cg.getClassName() + "." + method.getName());

                        //if(newType.equals("java.lang.String") && (type.equals("byte[]") || type.equals("char[]"))) {
                        if (newType.equals("java.lang.String") && type.contains("[]")) {
                            startLoc = i;
                            arrayLength = GenericMethods.getValueOfNumber(handles[i + 2].getInstruction(), cg.getConstantPool());
                            //logger.debug("Start Location for BASA replacement: " + startLoc);
                            //logger.debug("Length of Array: " + arrayLength);
                        }
                    }
                    if (startLoc >= 0 && arrayLength >= 1) {
                        if (handles.length > (i + 1)) {
                            if ((handles[i].getInstruction() instanceof BASTORE)
                                    && GenericMethods.isNumber(handles[i - 1].getInstruction())
                                    && GenericMethods.isNumber(handles[i - 2].getInstruction())
                                    && (handles[i - 3].getInstruction() instanceof DUP)) {
                                /*
                                                                                    TODO: Also you need to make the endLoc INVOKESTATIC STRING<init> or something because it is leaving a trailing command and fucking up.
                                                                                */
                                if (GenericMethods.isCall(handles[i + 1].getInstruction())) {
                                    if (GenericMethods.getCallArgTypes(handles[i + 1].getInstruction(), cg.getConstantPool()) != null) {
                                        Type[] tp = GenericMethods.getCallArgTypes(handles[i + 1].getInstruction(), cg.getConstantPool());
                                        String lasttp = tp[tp.length - 1].toString();
                                        if (!lasttp.contains(type)) {
                                            int tendLoc = GenericMethods.getValueOfNumber(handles[i - 2].getInstruction(), cg.getConstantPool());
                                            if (tendLoc == (arrayLength - 1)) {
                                                endLoc = i;
                                                //logger.debug("End Location for BASA replacement: " + endLoc);
                                            }
                                        } else {
                                            logger.debug("Can't replace String(" + type + "), following method call has argument of same type");
                                            startLoc = -1;
                                            endLoc = -1;
                                            arrayLength = -1;
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if ((startLoc >= 0) && (endLoc >= 0) && (arrayLength > 1)
                            && (endLoc < handles.length)) {
                        byte[] stringBytes = new byte[arrayLength];
                        int loc = 0;
                        for (int x = startLoc; x <= endLoc; x++) {
                            if (GenericMethods.isNumber(handles[x].getInstruction())
                                    && GenericMethods.isNumber(handles[x + 1].getInstruction())
                                    && (handles[x + 2].getInstruction() instanceof BASTORE)
                                    && (GenericMethods.getValueOfNumber(handles[x].getInstruction(), cg.getConstantPool()) == loc)) {
                                stringBytes[loc] = (byte) GenericMethods.getValueOfNumber(handles[x + 1].getInstruction(), cg.getConstantPool());
                                loc++;
                                if (loc == stringBytes.length) {
                                    if (replacing) {
                                        int stringRef = cg.getConstantPool().addString(new String(stringBytes));
                                        LDC lc = new LDC(stringRef);
                                        handles[startLoc].setInstruction(lc);
                                        for (int z = startLoc + 1; z <= endLoc; z++) {
                                            NOP nop = new NOP();
                                            handles[z].setInstruction(nop);
                                        }
                                        logger.debug("Replaced conversion with string " + new String(stringBytes) + " in " + cg.getClassName() + "." + method.getName());
                                    } else {
                                        logger.debug("Found conversion, string:" + new String(stringBytes));
                                    }
                                    startLoc = -1;
                                    endLoc = -1;
                                    arrayLength = -1;
                                    type = "";
                                    mg.setInstructionList(list);
                                    mg.removeNOPs();
                                    mg.setMaxLocals();
                                    mg.setMaxStack();
                                    cg.replaceMethod(method, mg.getMethod());
                                    continue;
                                }
                            }
                        }
                    }
                }
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
            for (ClassGen classIt : cgs.values()) {
                jos.putNextEntry(new JarEntry(classIt.getClassName().replace('.', '/') + ".class"));
                jos.write(classIt.getJavaClass().getBytes());
                jos.closeEntry();
                jos.flush();
            }
            for (JarEntry jbe : NonClassEntries.entries) {
                JarEntry destEntry = new JarEntry(jbe.getName());
                byte[] bite = IOUtils.toByteArray(NonClassEntries.ins.get(jbe));
                jos.putNextEntry(destEntry);
                jos.write(bite);
                jos.closeEntry();
            }
            jos.closeEntry();
            jos.close();
        } catch (IOException ioe) {
        }
    }

    public void transform() {
        logger.log("Generic String Fixer");
        logger.log("This hasn't been finished yet.");
        logger.log("Scanning/Replacing Strings hidden as byte arrays...");
        removeBASA();
        logger.log("Deobfuscation finished! Dumping jar...");
        dumpJar(JAR_NAME.replace(".jar", "") + "-deob.jar");
        logger.log("Operation Completed.");

    }
}
