package me.genericskid.gui.action.analysis;

import javassist.bytecode.ConstPool;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.MethodInfo;
import javassist.CtMethod;
import javassist.CtClass;
import java.util.zip.ZipEntry;
import javassist.ClassPath;
import javassist.ClassClassPath;
import javassist.ClassPool;

import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;
import java.awt.Desktop;
import me.genericskid.util.io.FileIO;
import me.genericskid.util.analyze.AnalysisRules;
import me.genericskid.gui.frames.EnumPanel;
import me.genericskid.gui.frames.panel.impl.AnalyzePanel;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.FrameMain;
import java.awt.event.ActionListener;

public class ActionSearchStrings implements ActionListener
{
    private final FrameMain instance;
    
    public ActionSearchStrings(final FrameMain instance) {
        this.instance = instance;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final File file = FrameMain.getFileObfu();
        final AnalyzePanel ap = (AnalyzePanel)this.instance.getPanel(EnumPanel.Analyze);
        if (file != null && file.exists()) {
            final ArrayList<String> lines = this.search(file, new AnalysisRules(ap.getSearchText(), ap.getPackages(), ap.isInclusive(), ap.isExclusive(), ap.isCaseSensitive()));
            final String fileName = file.getName() + "-" + ap.getSearchText().toString() + ".txt";
            FileIO.saveAllLines(fileName, lines);
            try {
                final Desktop d = Desktop.getDesktop();
                d.open(new File(fileName));
            }
            catch (Exception ex) {}
        }
    }
    
    private ArrayList<String> search(final File file, final AnalysisRules rules) {
        final ArrayList<String> lines = new ArrayList<>();
        try {
            final URLClassLoader child = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
            final ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            final ClassPool pool = ClassPool.getDefault();
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    className = className.substring(0, className.length() - ".class".length());
                    if (rules.isInclusiveSearch()) {
                        if (!this.allowedPackage(className, rules.getPackages())) {
                            continue;
                        }
                    }
                    else if (rules.isExclusiveSearch() && this.allowedPackage(className, rules.getPackages())) {
                        continue;
                    }
                    Class<?> classToLoad = null;
                    try {
                        classToLoad = child.loadClass(className);
                    }
                    catch (NoClassDefFoundError | IllegalAccessError noClassDefFoundError) {}
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (classToLoad != null) {
                        final ClassClassPath ccpath = new ClassClassPath(classToLoad);
                        pool.insertClassPath(ccpath);
                        final CtClass ctClass = pool.get(className);
                        CtMethod[] declaredMethods;
                        for (int length = (declaredMethods = ctClass.getDeclaredMethods()).length, j = 0; j < length; ++j) {
                            final CtMethod cm = declaredMethods[j];
                            final MethodInfo mi = cm.getMethodInfo();
                            if (mi != null) {
                                final CodeAttribute ca = mi.getCodeAttribute();
                                if (ca != null) {
                                    final ConstPool cp = ca.getConstPool();
                                    if (cp != null) {
                                        final int size = cp.getSize();
                                        if (size != 0) {
                                            for (int i = 0; i < size; ++i) {
                                                final String ldcText = String.valueOf(cp.getLdcValue(i));
                                                final String clazz = cp.getClassName();
                                                if (ldcText != null && !ldcText.isEmpty() && !ldcText.equalsIgnoreCase("null")) {
                                                    String[] searches;
                                                    for (int length2 = (searches = rules.getSearches()).length, k = 0; k < length2; ++k) {
                                                        final String text = searches[k];
                                                        if (rules.isCaseSensitivem()) {
                                                            if (!ldcText.contains(text)) {
                                                                continue;
                                                            }
                                                        }
                                                        else if (!ldcText.toLowerCase().contains(text.toLowerCase())) {
                                                            continue;
                                                        }
                                                        lines.add("[Method Constant] - " + clazz + " | " + mi.getName() + " | " + ldcText);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            zip.close();
        }
        catch (Exception e2) {
            System.out.println("RRRRRRRRRRRRRRRRRRRIP");
        }
        return lines;
    }
    
    private boolean allowedPackage(final String className, final String[] packages) {
        return false;
    }
}
