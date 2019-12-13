package net.contra.jmd.util;

import org.apache.bcel.generic.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class GenericMethods {
    //TODO: Get LDC_W into isInt and getValueOfInt
    public static boolean isNumber(Instruction ins) {
        return ins instanceof BIPUSH
                || ins instanceof SIPUSH
                || ins instanceof ICONST
                || ins instanceof LDC_W;
    }

    public static int getValueOfNumber(Instruction ins, ConstantPoolGen cpg) {
        if (ins instanceof BIPUSH) {
            return ((BIPUSH) ins).getValue().intValue();
        } else if (ins instanceof SIPUSH) {
            return ((SIPUSH) ins).getValue().intValue();
        } else if (ins instanceof ICONST) {
            return ((ICONST) ins).getValue().intValue();
        } else if (ins instanceof LDC_W) {
            LDC_W ldcw = (LDC_W) ins;
            return Integer.valueOf(ldcw.getValue(cpg).toString());
        } else {
            return -1;
        }
    }

    public static String getCallSignature(Instruction ins, ConstantPoolGen cp) {
        if (ins instanceof INVOKESTATIC) {
            INVOKESTATIC invst = (INVOKESTATIC) ins;
            return invst.getSignature(cp);
        } else if (ins instanceof INVOKEVIRTUAL) {
            INVOKEVIRTUAL invst = (INVOKEVIRTUAL) ins;
            return invst.getSignature(cp);
        } else if (ins instanceof INVOKEINTERFACE) {
            INVOKEINTERFACE invst = (INVOKEINTERFACE) ins;
            return invst.getSignature(cp);
        } else if (ins instanceof INVOKESPECIAL) {
            INVOKESPECIAL invst = (INVOKESPECIAL) ins;
            return invst.getSignature(cp);
        } else {
            return null;
        }
    }

    public static Instruction getNewInvoke(Instruction ins, int index) {
        if (ins instanceof INVOKESTATIC) {
            INVOKESTATIC invst = (INVOKESTATIC) ins;
            invst.setIndex(index);
            return invst;
        } else if (ins instanceof INVOKEVIRTUAL) {
            INVOKEVIRTUAL invst = (INVOKEVIRTUAL) ins;
            invst.setIndex(index);
            return invst;
        } else if (ins instanceof INVOKEINTERFACE) {
            INVOKEINTERFACE invst = (INVOKEINTERFACE) ins;
            invst.setIndex(index);
            return invst;
        } else if (ins instanceof INVOKESPECIAL) {
            INVOKESPECIAL invst = (INVOKESPECIAL) ins;
            invst.setIndex(index);
            return invst;
        } else {
            return null;
        }
    }

    public static String getCallReturnType(Instruction ins, ConstantPoolGen cp) {
        if (ins instanceof INVOKESTATIC) {
            INVOKESTATIC invst = (INVOKESTATIC) ins;
            return invst.getReturnType(cp).toString();
        } else if (ins instanceof INVOKEVIRTUAL) {
            INVOKEVIRTUAL invst = (INVOKEVIRTUAL) ins;
            return invst.getReturnType(cp).toString();
        } else if (ins instanceof INVOKEINTERFACE) {
            INVOKEINTERFACE invst = (INVOKEINTERFACE) ins;
            return invst.getReturnType(cp).toString();
        } else if (ins instanceof INVOKESPECIAL) {
            INVOKESPECIAL invst = (INVOKESPECIAL) ins;
            return invst.getReturnType(cp).toString();
        } else {
            return null;
        }
    }

    public static String getCallClassName(Instruction ins, ConstantPoolGen cp) {
        if (ins instanceof INVOKESTATIC) {
            INVOKESTATIC invst = (INVOKESTATIC) ins;
            return invst.getClassName(cp);
        } else if (ins instanceof INVOKEVIRTUAL) {
            INVOKEVIRTUAL invst = (INVOKEVIRTUAL) ins;
            return invst.getClassName(cp);
        } else if (ins instanceof INVOKEINTERFACE) {
            INVOKEINTERFACE invst = (INVOKEINTERFACE) ins;
            return invst.getClassName(cp);
        } else if (ins instanceof INVOKESPECIAL) {
            INVOKESPECIAL invst = (INVOKESPECIAL) ins;
            return invst.getClassName(cp);
        } else {
            return null;
        }
    }

    public static void dumpJar(String path, Collection<ClassGen> cgs) {
        FileOutputStream os;
        path = path.replace(".jar", "") + "-deob.jar";
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

            for (ClassGen classIt : cgs) {
                JarEntry classEntry = new JarEntry(classIt.getClassName().replace('.', '/') + ".class");
                jos.putNextEntry(classEntry);
                jos.write(classIt.getJavaClass().getBytes());
                jos.closeEntry();
            }
            jos.closeEntry();
            jos.close();
        } catch (IOException ignored) {
        }
    }

    public static String getCallMethodName(Instruction ins, ConstantPoolGen cp) {
        if (ins instanceof INVOKESTATIC) {
            INVOKESTATIC invst = (INVOKESTATIC) ins;
            return invst.getMethodName(cp);
        } else if (ins instanceof INVOKEVIRTUAL) {
            INVOKEVIRTUAL invst = (INVOKEVIRTUAL) ins;
            return invst.getMethodName(cp);
        } else if (ins instanceof INVOKEINTERFACE) {
            INVOKEINTERFACE invst = (INVOKEINTERFACE) ins;
            return invst.getMethodName(cp);
        } else if (ins instanceof INVOKESPECIAL) {
            INVOKESPECIAL invst = (INVOKESPECIAL) ins;
            return invst.getMethodName(cp);
        } else {
            return null;
        }
    }

    public static Type[] getCallArgTypes(Instruction ins, ConstantPoolGen cp) {
        if (ins instanceof INVOKESTATIC) {
            INVOKESTATIC invst = (INVOKESTATIC) ins;
            return invst.getArgumentTypes(cp);
        } else if (ins instanceof INVOKEVIRTUAL) {
            INVOKEVIRTUAL invst = (INVOKEVIRTUAL) ins;
            return invst.getArgumentTypes(cp);
        } else if (ins instanceof INVOKEINTERFACE) {
            INVOKEINTERFACE invst = (INVOKEINTERFACE) ins;
            return invst.getArgumentTypes(cp);
        } else if (ins instanceof INVOKESPECIAL) {
            INVOKESPECIAL invst = (INVOKESPECIAL) ins;
            return invst.getArgumentTypes(cp);
        } else {
            return null;
        }
    }

    public static boolean isCall(Instruction ins) {
        return ins instanceof INVOKESTATIC
                || ins instanceof INVOKEVIRTUAL
                || ins instanceof INVOKEINTERFACE
                || ins instanceof INVOKESPECIAL;
    }
}
