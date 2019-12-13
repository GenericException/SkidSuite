package net.contra.jmd.transformers.generic;

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

public class Renamer {
    private static LogHandler logger = new LogHandler("Renamer");
    private Map<String, ClassGen> cgs = new HashMap<String, ClassGen>();
    private Map<String, ClassGen> tempcgs = new HashMap<String, ClassGen>();
    private Map<String, String> methodNames = new HashMap<String, String>(); //OldName, NewName
    String JAR_NAME;

    public Renamer(String jarfile) throws Exception {
        File jar = new File(jarfile);
        JAR_NAME = jarfile;
        JarFile jf = new JarFile(jar);
        Enumeration<JarEntry> entries = jf.entries();
        //TODO: Make it not rename the main class
        //TODO: Keep it from renaming like, methods that shouldn't be renamed and shit??
        //TODO: Rename FIELDS MOTHERFUCKER
        //Manifest jm = jf.getManifest();
        //if(jm.getAttributes("Main-class") != null &&
        //logger.debug("Found main class for jar: " + );
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

    public void renameClasses() {
        int classCount = 1;
        for (ClassGen cg : cgs.values()) {
            String className = cg.getClassName();
            String shortClassName = className.substring(className.lastIndexOf(".") + 1, className.length());
            String newClassName = className.replace(shortClassName, "Class" + classCount);
            cg.setClassName(newClassName);
            tempcgs.put(newClassName, cg);
            classCount++;
        }
        if (classCount > 1) {
            logger.debug("Renamed " + classCount + " classes.");
            cgs = tempcgs;
        }
    }

    public void replaceMethodRefs() {
        for (ClassGen cg : cgs.values()) {
            for (Method m : cg.getMethods()) {
                int replaced = 0;
                MethodGen mg = new MethodGen(m, cg.getClassName(), cg.getConstantPool());
                InstructionList list = mg.getInstructionList();
                if (list == null) {
                    continue;
                }
                InstructionHandle[] handles = list.getInstructionHandles();
                for (InstructionHandle handle : handles) {
                    if (GenericMethods.isCall(handle.getInstruction())) {
                        String oldClassName = GenericMethods.getCallClassName(handle.getInstruction(), cg.getConstantPool());
                        String oldMethodName = GenericMethods.getCallMethodName(handle.getInstruction(), cg.getConstantPool());
                        String oldSignature = GenericMethods.getCallSignature(handle.getInstruction(), cg.getConstantPool());
                        String mod = oldClassName + "-" + oldMethodName + "-" + oldSignature;
                        if (methodNames.containsKey(mod)) {
                            //logger.debug("Accessing " + methodNames.get(mod));
                            String[] args = methodNames.get(mod).split("-");
                            String newClassName = args[0];
                            String newMethodName = args[1];
                            String newSignature = args[2];
                            int newindex = cg.getConstantPool().addMethodref(newClassName, newMethodName, newSignature);
                            Instruction newInvoke = GenericMethods.getNewInvoke(handle.getInstruction(), newindex);
                            handle.setInstruction(newInvoke);
                            replaced++;
                        }
                    }
                }
                mg.setInstructionList(list);
                mg.setMaxLocals();
                mg.setMaxStack();
                if (replaced > 0) {
                    logger.debug("replaced " + replaced + " methodrefs in " + m.getName());
                    cg.replaceMethod(m, mg.getMethod());
                }
            }
        }
    }

    public void renameMethods() {
        for (ClassGen cg : cgs.values()) {
            if (cg.isAbstract() || cg.isInterface()) {
                continue;
            }
            int count = 1;
            for (Method m : cg.getMethods()) {
                if (m.getName().equalsIgnoreCase("<clinit>")
                        || m.getName().equalsIgnoreCase("<init>")
                        || m.getName().equalsIgnoreCase("main")) {
                    continue;
                }
                ConstantPoolGen cpg = cg.getConstantPool();
                String name = "";
                if (m.isStatic()) {
                    name += "static";
                }
                String type = m.getReturnType().toString();

                name += type.substring(type.lastIndexOf(".") + 1, type.length());
                name = name.replace("void", "");
                if (name.contains("[]")) {
                    name = name.replace("[]", "Array");
                }
                name += "Method" + count;
                //TODO: Get it to fully change the name (updated methodref name index) and not corrupt the constant pool lol
                //TODO: Rename classes first, then methods, then fields.
                MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
                mg.setName(name);
                cg.replaceMethod(m, mg.getMethod());
                cg.setConstantPool(cpg);
                //y.pb.methodsig - Class.Method04.MethodSig
                methodNames.put(cg.getClassName() + "-" + m.getName() + "-" + m.getSignature(), cg.getClassName() + "-" + name + "-" + m.getSignature());
                count++;
                logger.debug(cg.getClassName() + "." + m.getName() + " -> " + cg.getClassName() + "." + name);
            }
        }
    }

    public void transform() {
        logger.log("Generic Renamer");
        renameClasses();
        renameMethods();
        replaceMethodRefs();
        logger.log("Deobfuscation finished! Dumping jar...");
        GenericMethods.dumpJar(JAR_NAME, cgs.values());
        logger.log("Operation Completed.");

    }
}
