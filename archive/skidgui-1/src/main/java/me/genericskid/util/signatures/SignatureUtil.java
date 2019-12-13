// Recompile not supported in read-only mode, please use a JDK
package me.genericskid.util.signatures;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.genericskid.util.io.FileIO;

public class SignatureUtil {
    private static final String ErrType = "{|-}";

    public static ArrayList populateSigs(String jarFile, SignatureRules rules) throws IOException {
        boolean inclusive = rules.isInclusive();
        boolean ignoreZKMs = rules.isIgnoringZKM();
        boolean checkObfIDs = rules.containsObfIDs();
        ArrayList sigs = new ArrayList();
        URLClassLoader child = URLClassLoader.newInstance(new URL[]{(new File(jarFile)).toURI().toURL()});
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));

        for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace('/', '.');
                className = className.substring(0, className.length() - ".class".length());
                if (!className.contains("$") && (!inclusive || inclusive && allowedPackage(className, rules.getPackages()))) {
                    try {
                        Class classToLoad = child.loadClass(className);
                        Field[] declaredFields = null;

                        try {
                            declaredFields = classToLoad.getDeclaredFields();
                        } catch (VerifyError var42) {
                        }

                        if (declaredFields != null) {
                            int fieldTypes = 0;
                            int fieldCount = 0;
                            int fieldMods = 0;
                            String fieldTypeStr = "";
                            int i = 0;
                            int possibleObfIDs = 0;
                            int endl = declaredFields.length;
                            boolean hasObfStrings = false;
                            int obfStringMods = 0;
                            Field[] var24 = declaredFields;
                            int methodMods = declaredFields.length;

                            int methodCount;
                            for(methodCount = 0; methodCount < methodMods; ++methodCount) {
                                Field f = var24[methodCount];
                                ++i;
                                Type t = f.getType();
                                boolean isString = t.getTypeName().contains("String");
                                boolean isFinal = (f.getModifiers() & 16) == 16;
                                boolean isPrivate = (f.getModifiers() & 2) == 2;
                                boolean isStatic = (f.getModifiers() & 8) == 8;
                                if (isString && isFinal && isPrivate && isStatic) {
                                    obfStringMods = f.getModifiers();
                                    ++possibleObfIDs;
                                }

                                if (i == endl && ignoreZKMs) {
                                    boolean isStringArray = t.getTypeName().contains("String[]");
                                    if (isStringArray && isFinal && isPrivate && isStatic) {
                                        hasObfStrings = true;
                                        break;
                                    }
                                }

                                ++fieldCount;
                                fieldMods += f.getModifiers();
                                if (!fieldTypeStr.contains(t.getTypeName())) {
                                    fieldTypeStr = fieldTypeStr + t.getTypeName();
                                    ++fieldTypes;
                                }
                            }

                            if (checkObfIDs && possibleObfIDs > 1 && !hasObfStrings) {
                                fieldMods -= obfStringMods;
                                --fieldCount;
                            }

                            int methodTypes = 0;
                            methodCount = 0;
                            methodMods = 0;
                            int methodParams = 0;
                            String methodTypeStr = "";
                            Method[] declaredMethods = classToLoad.getDeclaredMethods();
                            Method[] var54 = declaredMethods;
                            int constructCount = declaredMethods.length;

                            int classMods;
                            for(classMods = 0; classMods < constructCount; ++classMods) {
                                Method m = var54[classMods];
                                ++methodCount;
                                methodParams += m.getParameterTypes().length;
                                methodMods += m.getModifiers();
                                Type t = m.getGenericReturnType();
                                if (!methodTypeStr.contains(t.getTypeName())) {
                                    methodTypeStr = methodTypeStr + t.getTypeName();
                                    ++methodTypes;
                                }
                            }

                            int parentLevel = 0;

                            for(Class tmpClass = classToLoad; tmpClass != Object.class && tmpClass.getSuperclass() != null; tmpClass = tmpClass.getSuperclass()) {
                                ++parentLevel;
                            }

                            classMods = classToLoad.getModifiers();
                            constructCount = classToLoad.getConstructors().length;
                            int constructMods = 0;
                            int constructParams = 0;
                            int paramMods = 0;
                            Constructor[] constructors = classToLoad.getConstructors();
                            Constructor[] var37 = constructors;
                            int var36 = constructors.length;

                            for(int var35 = 0; var35 < var36; ++var35) {
                                Constructor con = var37[var35];
                                constructMods += con.getModifiers();
                                constructParams += con.getParameterCount();

                                try {
                                    Parameter[] parameters;
                                    int length4 = (parameters = con.getParameters()).length;

                                    for(int l = 0; l < length4; ++l) {
                                        Parameter par = parameters[l];
                                        paramMods += par.getModifiers();
                                    }
                                } catch (MalformedParametersException var43) {
                                }
                            }

                            Signature sig = new Signature(className, classMods, constructCount, constructMods, constructParams, paramMods, fieldTypes, fieldCount, fieldMods, methodTypes, methodCount, methodMods, methodParams, parentLevel);
                            boolean add = true;

                            for(Object sig1 : sigs) {
                                Signature s = (Signature) sig1;
                                if(s.toSig().equalsIgnoreCase(sig.toSig())) {
                                    add = false;
                                    break;
                                }
                            }

                            if (add) {
                                sigs.add(sig);
                            }
                        }
                    } catch (NoClassDefFoundError | ClassNotFoundException | IncompatibleClassChangeError var44) {
                    }
                }
            }
        }

        zip.close();
        return sigs;
    }

    private static HashMap getCLMap() {
        HashMap ret = new HashMap();
        List cllist = FileIO.loadAllLines("cllist.txt");

        for(Object o : cllist) {
            String s = (String) o;
            String[] split = s.split("=");
            if(split.length == 2) {
                String mc = split[0];
                String cl = split[1];
                ret.put(cl, mc);
            }
        }

        return ret;
    }

    private static boolean allowedPackage(String className, String[] packages) {
        String[] var5 = packages;
        int var4 = packages.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            String packageStr = var5[var3];
            if (className.contains(packageStr)) {
                return true;
            }
        }

        return false;
    }

    private static Signature getSignature(String className, ArrayList sigs) {

        for(Object sig1 : sigs) {
            Signature sig = (Signature) sig1;
            if(sig.getOwner().equals(className)) {
                return sig;
            }
        }

        return null;
    }

    public static boolean isSig(String s) {
        String regex = "^(?:[0-9]{1,4}:){11}[0-9]{1,3}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        return m.find();
    }

    public static void compareSigs2(ArrayList origList, ArrayList newList, String jarFileObfu, boolean fieldsAndMethods, String jarFileOrig, boolean checkCL, boolean safe) {
        ArrayList matchedSigs = new ArrayList();

        try {
            URLClassLoader obfuLoader = URLClassLoader.newInstance(new URL[]{(new File(jarFileObfu)).toURI().toURL()});
            URLClassLoader origLoader = URLClassLoader.newInstance(new URL[]{(new File(jarFileOrig)).toURI().toURL()});
            ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFileObfu));

            for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    className = className.substring(0, className.length() - ".class".length());
                    if (!className.contains("$")) {
                        try {
                            if (checkCL) {
                                HashMap clmap = getCLMap();
                                Class classObfu = obfuLoader.loadClass(className);
                                if (classObfu != null) {
                                    Field[] var18;
                                    int var38 = (var18 = classObfu.getDeclaredFields()).length;

                                    for(int var37 = 0; var37 < var38; ++var37) {
                                        Field f = var18[var37];
                                        boolean isString = f.getGenericType().getTypeName().equals("java.lang.String");
                                        boolean isFinal = (f.getModifiers() & 16) == 16;
                                        boolean isPrivate = (f.getModifiers() & 2) == 2;
                                        boolean isStatic = (f.getModifiers() & 8) == 8;
                                        boolean isAbs = (f.getModifiers() & 1024) == 1024;
                                        if (isString && isFinal && isPrivate && isStatic && !isAbs) {
                                            try {
                                                f.setAccessible(true);
                                                String clVal = (String)f.get(null);
                                                if (clVal.contains("CL_")) {
                                                    String mc = (String)clmap.get(clVal);
                                                    boolean hasPackage = className.contains(".");
                                                    String mappingsLine = (!hasPackage ? "CLASS none/" : "CLASS ") + className.replace(".", "/") + " " + mc;
                                                    if (!matchedSigs.contains(mappingsLine)) {
                                                        matchedSigs.add(mappingsLine);
                                                        if (fieldsAndMethods) {
                                                            matchedSigs.addAll(getMethodsFields(origLoader, obfuLoader, className, mc, safe));
                                                        }
                                                    }
                                                }
                                            } catch (AbstractMethodError | IllegalAccessException | IllegalArgumentException var29) {
                                            }
                                        }
                                    }
                                }
                            } else {
                                Signature sig = getSignature(className, newList);
                                if (sig != null) {

                                    for(Object o : origList) {
                                        Signature orgSig = (Signature) o;
                                        if(sig.toSig().equals(orgSig.toSig())) {
                                            boolean hasPackage = sig.getOwner().contains(".");
                                            String mappingsLine = (!hasPackage ? "CLASS none/" :
                                                    "CLASS ") + sig.getOwner().replace(".", "/") + " " + orgSig.getOwner().replace(".", "/");
                                            if(!matchedSigs.contains(mappingsLine)) {
                                                matchedSigs.add(mappingsLine);
                                                if(fieldsAndMethods) {
                                                    matchedSigs.addAll(getMethodsFields(origLoader, obfuLoader, className, orgSig.getOwner().replace(".", "/"), safe));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (RuntimeException | ExceptionInInitializerError | ClassNotFoundException | NoClassDefFoundError var30) {
                        }
                    }
                }
            }

            zip.close();
        } catch (IOException var31) {
            var31.printStackTrace();
        }

        String mappings = jarFileObfu + ".mapping";
        FileIO.saveAllLines(mappings, matchedSigs);

        try {
            Desktop d = Desktop.getDesktop();
            d.open(new File(mappings));
        } catch (Exception var28) {
        }

    }

    private static Collection getMethodsFields(URLClassLoader origLoader, URLClassLoader obfuLoader, String obfuName, String clearName, boolean safe) {
        ArrayList matchedSigs = new ArrayList();
        Class classObfu = null;
        Class classOrig = null;

        try {
            classObfu = obfuLoader.loadClass(obfuName.replaceAll("/", "."));
            classOrig = origLoader.loadClass(clearName.replaceAll("/", ".").replaceAll("_", "$"));
        } catch (IllegalArgumentException | NoClassDefFoundError | ClassNotFoundException var28) {
        }

        if (classObfu != null && classOrig != null) {
            Field[] fields1 = classObfu.getDeclaredFields();
            Field[] fields2 = classOrig.getDeclaredFields();
            if (fields1.length != fields2.length) {
                return matchedSigs;
            } else {
                ArrayList usedNamesF = new ArrayList();
                ArrayList typesF = new ArrayList();

                for(int i = 0; i < fields1.length; ++i) {
                    Field f1 = fields1[i];
                    Field f2 = fields2[i];
                    String fld = "\t";
                    f1.setAccessible(true);
                    f2.setAccessible(true);
                    String f1Name = f1.getName();
                    String f2Name = f2.getName();
                    if (!f1Name.contains("$") && !f2Name.contains("$") && !usedNamesF.contains(f2Name)) {
                        usedNamesF.add(f2Name);
                        fld = fld + "FIELD " + f1Name + " " + f2Name + " " + getTypeChar(f1.getGenericType().getTypeName());
                        if (safe) {
                            String check = f1Name + getTypeChar(f1.getGenericType().getTypeName());
                            if (typesF.contains(check)) {
                                continue;
                            }

                            typesF.add(check);
                        }

                        if (!fld.equals("\t") && !matchedSigs.contains(fld)) {
                            matchedSigs.add(fld);
                        }
                    }
                }

                Method[] meth1 = classObfu.getDeclaredMethods();
                Method[] meth2 = classOrig.getDeclaredMethods();
                if (meth1.length != meth2.length) {
                    return matchedSigs;
                } else {
                    ArrayList usedNamesM = new ArrayList();
                    ArrayList typesM = new ArrayList();

                    for(int i = 0; i < meth1.length; ++i) {
                        Method m1 = meth1[i];
                        Method m2 = meth2[i];
                        String mth = "\t";
                        m1.setAccessible(true);
                        m2.setAccessible(true);
                        String m1Name = m1.getName();
                        String m2Name = m2.getName();
                        if (!m1Name.contains("$") && !m2Name.contains("$") && !usedNamesM.contains(m2Name)) {
                            usedNamesM.add(m2Name);
                            String methType = m1.getGenericReturnType().getTypeName();
                            mth = mth + "METHOD " + m1Name + " " + m2Name + " ";
                            String typeStr = "";
                            if (m1.getParameterCount() == 0) {
                                typeStr = typeStr + "()" + getTypeChar(m1.getGenericReturnType().getTypeName());
                            } else {
                                typeStr = typeStr + "(";
                                Parameter[] var27;
                                int var26 = (var27 = m1.getParameters()).length;

                                for(int var25 = 0; var25 < var26; ++var25) {
                                    Parameter p = var27[var25];
                                    typeStr = typeStr + getTypeChar(p.getParameterizedType().getTypeName());
                                }

                                typeStr = typeStr + ")" + getTypeChar(methType);
                            }

                            mth = mth + typeStr;
                            if (safe) {
                                if (typesM.contains(m1Name + typeStr)) {
                                    continue;
                                }

                                typesM.add(m1Name + typeStr);
                            }

                            if (!mth.equals("\t") && !mth.contains("{|-}") && !matchedSigs.contains(mth)) {
                                matchedSigs.add(mth);
                            }
                        }
                    }

                    return matchedSigs;
                }
            }
        } else {
            return matchedSigs;
        }
    }

    public static String getTypeChar(String type) {
        switch(type.hashCode()) {
            case -1374008726:
                if (type.equals("byte[]")) {
                    return "[B";
                }
                break;
            case -1361632968:
                if (type.equals("char[]")) {
                    return "[C";
                }
                break;
            case -1325958191:
                if (type.equals("double")) {
                    return "D";
                }
                break;
            case -1097129250:
                if (type.equals("long[]")) {
                    return "[J";
                }
                break;
            case -766441794:
                if (type.equals("float[]")) {
                    return "[F";
                }
                break;
            case 104431:
                if (type.equals("int")) {
                    return "I";
                }
                break;
            case 3039496:
                if (type.equals("byte")) {
                    return "B";
                }
                break;
            case 3052374:
                if (type.equals("char")) {
                    return "C";
                }
                break;
            case 3327612:
                if (type.equals("long")) {
                    return "J";
                }
                break;
            case 3625364:
                if (type.equals("void")) {
                    return "V";
                }
                break;
            case 64711720:
                if (type.equals("boolean")) {
                    return "Z";
                }
                break;
            case 97526364:
                if (type.equals("float")) {
                    return "F";
                }
                break;
            case 100361105:
                if (type.equals("int[]")) {
                    return "[I";
                }
                break;
            case 109413500:
                if (type.equals("short")) {
                    return "S";
                }
                break;
            case 1359468275:
                if (type.equals("double[]")) {
                    return "[D";
                }
                break;
            case 2058423690:
                if (type.equals("boolean[]")) {
                    return "[Z";
                }
                break;
            case 2067161310:
                if (type.equals("short[]")) {
                    return "[S";
                }
        }

        if (!type.contains(".")) {
            return "{|-}";
        } else {
            if (type.endsWith("[]")) {
                type = type.substring(0, type.length() - 2);
            }

            String out = "L" + type.replaceAll("\\.", "/") + ";";
            return out.contains("]") ? "{|-}" : out;
        }
    }
}
