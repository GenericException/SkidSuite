package me.genericskid.gui.action.drag;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.net.URL;
import me.genericskid.gui.frames.FrameMain;

import javax.swing.JOptionPane;
import me.genericskid.util.node.DefaultNode;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.Dimension;
import me.genericskid.gui.component.drag.DragContainer;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import me.genericskid.gui.frames.panel.impl.RelationshipPanel;
import me.genericskid.gui.component.drag.DragBox;
import me.genericskid.util.ClassContainer;
import java.util.HashMap;
import java.awt.event.ActionListener;

public abstract class DragActionListener implements ActionListener
{
    protected final HashMap<ClassContainer, DragBox> classMap;
    protected final RelationshipPanel panel;
    protected final DragBox daBox;
    private final List<String> names;
    
    public DragActionListener(final RelationshipPanel panel) {
        this.classMap = new HashMap<>();
        this.names = new ArrayList<>();
        this.panel = panel;
        this.daBox = null;
    }
    
    public DragActionListener(final DragBox box) {
        this.classMap = new HashMap<>();
        this.names = new ArrayList<>();
        this.panel = null;
        this.daBox = box;
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
        final DragContainer container = new DragContainer();
        final Dimension defaultSize = new Dimension(800, 600);
        container.setSize(defaultSize);
        container.setPreferredSize(defaultSize);
        this.init(container);
        final JFrame frame = new JFrame();
        frame.setSize(defaultSize);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(container, "Center");
        frame.setVisible(true);
    }
    
    protected void init(final DragContainer container) {
        if (this.panel == null) {
            this.populateMap(this.daBox.classContainer.clazz, container);
        }
        else {
            final ArrayList<DefaultNode> nodes = this.panel.getSelected();
            this.classMap.clear();
            if (nodes.size() > 0) {
                final String originClasspath = this.getClassPath(nodes.get(0));
                this.populateMap(this.getClassFromPath(originClasspath), container);
            }
        }
    }
    
    protected void populateMap(final Class<?> originClass, final DragContainer container) {
        if (originClass == null) {
            JOptionPane.showMessageDialog(null, "Could not locate class");
            return;
        }
        this.genrateContainers(originClass);
        for (final DragBox dragbox : this.classMap.values()) {
            dragbox.setParent(container);
            for (final ClassContainer cont : this.classMap.keySet()) {
                if (dragbox.classContainer.clazz.getSuperclass() != null && dragbox.classContainer.clazz.getSuperclass().equals(cont.clazz)) {
                    dragbox.parent = this.classMap.get(cont);
                }
                if (cont.clazz.getSuperclass() != null && cont.clazz.getSuperclass().equals(dragbox.classContainer.clazz)) {
                    dragbox.children.add(this.classMap.get(cont));
                }
                if (cont.clazz != null && dragbox.classContainer.clazz != null) {
                    Class<?>[] interfaces;
                    for (int length = (interfaces = dragbox.classContainer.clazz.getInterfaces()).length, i = 0; i < length; ++i) {
                        final Class<?> boxInter = interfaces[i];
                        if (boxInter.equals(cont.clazz)) {
                            dragbox.interfaces.add(this.classMap.get(cont));
                        }
                    }
                }
            }
        }
        for (final DragBox dragbox : this.classMap.values()) {
            container.addBox(dragbox);
        }
        container.showBoxes();
    }
    
    protected abstract void genrateContainers(final Class<?> p0);
    
    protected void addContainer(final ClassContainer c) {
        if (c != null) {
            final String name = c.clazz.getName();
            if (!this.names.contains(name)) {
                this.classMap.put(c, this.createDragBox(c));
                Class<?>[] interfaces;
                for (int length = (interfaces = c.clazz.getInterfaces()).length, j = 0; j < length; ++j) {
                    final Class<?> interfaze = interfaces[j];
                    final ClassContainer i = new ClassContainer(interfaze);
                    this.classMap.put(i, this.createDragBox(i));
                }
                this.names.add(name);
            }
        }
    }
    
    protected DragBox createDragBox(final ClassContainer c) {
        final DragBox cr = new DragBox(c);
        cr.setSize(200, 40);
        return cr;
    }
    
    protected String getClassPath(final DefaultNode n) {
        final StringBuilder sb = new StringBuilder(n.getNewName());
        for (DefaultNode nn = (DefaultNode)n.getParent(); nn != null && nn.getParent() != null; nn = (DefaultNode)nn.getParent()) {
            sb.insert(0, nn.getNewName() + "/");
        }
        return sb.toString();
    }
    
    protected Class<?> getClassFromPath(final String s) {
        URLClassLoader loader = null;
        final File file = FrameMain.getFileObfu();
        try {
            loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
            String className = s;
            className = className.replace('/', '.');
            className = className.substring(0, className.length() - ".class".length());
            if (className.contains("$")) {
                return null;
            }
            try {
                final Class<?> clazz = loader.loadClass(className);
                if (clazz == null) {
                    return null;
                }
                return clazz;
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }
}
