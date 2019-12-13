package me.genericskid.gui.action.drag;

import java.util.zip.ZipEntry;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import me.genericskid.util.ClassContainer;

import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.net.URLClassLoader;
import me.genericskid.gui.frames.FrameMain;
import java.net.URL;
import me.genericskid.gui.component.drag.DragBox;
import me.genericskid.gui.frames.panel.impl.RelationshipPanel;

public class ActionShowChildren extends DragActionListener
{
    public ActionShowChildren(final RelationshipPanel panel) {
        super(panel);
    }
    
    public ActionShowChildren(final DragBox box) {
        super(box);
    }
    
    @Override
    protected void genrateContainers(final Class<?> originClass) {
        try {
            final URLClassLoader loader = URLClassLoader.newInstance(new URL[] { FrameMain.getFileObfu().toURI().toURL() });
            final ZipInputStream zip = new ZipInputStream(new FileInputStream(FrameMain.getFileObfu()));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory()) {
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.');
                        className = className.substring(0, className.length() - ".class".length());
                        if (!className.contains("$")) {
                            try {
                                final Class<?> clazz = loader.loadClass(className);
                                final Class<?> dank = loader.loadClass(originClass.getName());
                                if (dank.isAssignableFrom(clazz)) {
                                    this.addContainer(new ClassContainer(clazz));
                                }
                            }
                            catch (NoClassDefFoundError | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
	}
}
