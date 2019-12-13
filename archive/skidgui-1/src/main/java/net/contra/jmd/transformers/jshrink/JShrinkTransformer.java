package net.contra.jmd.transformers.jshrink;

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

public class JShrinkTransformer implements Transformer {
    private static LogHandler logger = new LogHandler("JShrinkTransformer");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    String JAR_NAME;
    ClassGen LoaderClass = null;

    public JShrinkTransformer(String jarfile) throws Exception {
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
                if (isLoader(cg) && LoaderClass == null) {
                    logger.debug("Found JShrink Loader! " + cg.getClassName());
                    LoaderClass = cg;
                } else {
                    cgs.put(className, cg);
                }
            } else {
                if (!entry.isDirectory()) {
                    NonClassEntries.add(entry, jf.getInputStream(entry));
                }
            }
        }
        logger.debug("Classes loaded from JAR");
        if (LoaderClass == null) {
            logger.error("Loader class not found! Class not using JShrink");
        }
        jf.close();
    }

    public boolean isLoader(ClassGen cg) {


        return cg.getMethods().length == 3 && cg.getMethods()[1].isStatic()
                && cg.getMethods()[1].isFinal()
                && cg.getMethods()[1].isPublic()
                && cg.getMethods()[1].isSynchronized()
                && cg.getMethods()[1].getReturnType().toString().equals("java.lang.String");
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
                    if (handles[i].getInstruction() instanceof INVOKESTATIC && GenericMethods.isNumber(handles[i - 1].getInstruction())) {
                        INVOKESTATIC methodCall = (INVOKESTATIC) handles[i].getInstruction();
                        if (methodCall.getClassName(cg.getConstantPool()).contains(LoaderClass.getClassName())) {
                            int push = GenericMethods.getValueOfNumber(handles[i - 1].getInstruction(), cg.getConstantPool());
                            String decryptedString = StoreHandler.I(push);
                            if (decryptedString != null) {
                                int stringRef = cg.getConstantPool().addString(decryptedString);
                                LDC lc = new LDC(stringRef);
                                NOP nop = new NOP();
                                handles[i].setInstruction(lc);
                                handles[i - 1].setInstruction(nop);
                                replaced++;
                            }
                        }
                    }
                }
                mg.setInstructionList(list);
                mg.removeNOPs();
                mg.setMaxLocals();
                mg.setMaxStack();
                cg.replaceMethod(method, mg.getMethod());
            }
            if (replaced > 0) {
                logger.debug("replaced " + replaced + " calls in class " + cg.getClassName());
            }
        }
    }


    public void transform() {
        try {
            logger.log("Starting String Replacer...");
            replaceStrings();
        } catch (TargetLostException e) {
            e.printStackTrace();
        }
        logger.log("Deobfuscation Finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");
    }
}
