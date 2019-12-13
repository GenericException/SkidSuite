package me.genericskid.gui.frames.panel;

import java.util.*;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.zip.ZipFile;
import me.genericskid.util.node.Node;
import me.genericskid.gui.component.SkidTreeNode;

import javax.swing.JScrollPane;

import me.genericskid.gui.component.SelectionListener;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import me.genericskid.gui.frames.FrameMain;
import me.genericskid.gui.component.JTreeSkid;
import javax.swing.tree.TreePath;
import java.io.File;
import me.genericskid.util.node.DefaultNode;

public abstract class TreePanel extends BasicPanel
{
    private static final long serialVersionUID = 1L;
    protected DefaultNode mapping;
    protected File lastJar;
    protected TreePath lastPath;
    protected JTreeSkid tree;
    
    public TreePanel(final FrameMain frame) {
        super(frame);
        this.init();
    }
    
    @Override
    protected JPanel makeInfoPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        (this.tree = this.makeTree()).addTreeSelectionListener(new SelectionListener(this));
        final JScrollPane scroll = new JScrollPane(this.tree);
        panel.add(scroll, "Center");
        return panel;
    }
    
    public JTreeSkid makeTree() {
        final File jarObfu = FrameMain.getFileObfu();
        if (jarObfu == null) {
            final SkidTreeNode root = new SkidTreeNode(new DefaultNode("Load a jar", null));
            final SkidTreeNode child1 = new SkidTreeNode(new DefaultNode("You'll see stuff here", null));
            final SkidTreeNode child2 = new SkidTreeNode(new DefaultNode("Like class files and shit", null));
            root.add(child1);
            root.add(child2);
            return new JTreeSkid(root);
        }
        final SkidTreeNode root = new SkidTreeNode(new Node(FrameMain.getFileObfu().getName(), null));
        if (this.lastJar != jarObfu) {
            try {
                final DefaultNode base = this.createNode(new ZipFile(FrameMain.getFileObfu()));
                if (base != null) {
                    for (final DefaultNode n : base.getChildren()) {
                        root.add(this.getTreeNode(n));
                    }
                }
                else {
                    final SkidTreeNode child3 = new SkidTreeNode(new DefaultNode("Base is null", null));
                    root.add(child3);
                }
                this.mapping = base;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (this.mapping != null) {
            for (final DefaultNode n2 : this.mapping.getChildren()) {
                root.add(this.getTreeNode(n2));
            }
        }
        else {
            final SkidTreeNode child1 = new SkidTreeNode(new DefaultNode("Base is null", null));
            root.add(child1);
        }
        this.lastJar = jarObfu;
        return new JTreeSkid(root);
    }
    
    private SkidTreeNode getTreeNode(final DefaultNode init) {
        final SkidTreeNode dmt = new SkidTreeNode(init);
        if (init.getParent() == null) {
            System.out.println(init.getOldName() + "///" + init.getNewName());
        }
        for (final DefaultNode node : init.getChildren()) {
            dmt.add(this.getTreeNode(node));
        }
        for (final DefaultNode node : init.getFiles()) {
            dmt.add(new SkidTreeNode(node));
        }
        return dmt;
    }
    
    private DefaultNode createNode(final ZipFile zipFile) {
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        final DefaultNode top = new DefaultNode(zipFile.getName(), null);
        final Map<String, Map<String, Map>> map = new HashMap<>();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            if (!entry.getName().endsWith(".class")) {
                continue;
            }
            final String[] split = entry.getName().split("/");
            Map<String, Map> hn = new HashMap<>();
            for (int i = 0; i < split.length; ++i) {
                for (int j = 0; j < i + 1; ++j) {
                    final String v = split[j];
                    if (j == 0) {
                        hn = map.get(v);
                        if (hn == null) {
                            map.put(v, new HashMap<>());
                            hn = map.get(v);
                        }
                    }
                    else {
                        hn.computeIfAbsent(v, k -> new HashMap<>());
                        hn = hn.get(v);
                    }
                }
            }
        }
        final Set<String> keys = map.keySet();
        for (final String key : keys) {
            top.addChild(this.recurseAdd(map.get(key), key, top));
        }
        return top;
    }
    
    private DefaultNode recurseAdd(final Object o, final String name, final DefaultNode parent) {
        final DefaultNode retNode = new DefaultNode(name, parent);
        if (o instanceof HashMap) {
            final HashMap<String, ?> map = (HashMap<String, ?>)o;
            final Set<String> keys = map.keySet();
            for (final String key : keys) {
                retNode.addChild(this.recurseAdd(map.get(key), key, retNode));
            }
        }
        else {
            final String s = o.toString();
            retNode.addChild(new DefaultNode(s, retNode));
        }
        return retNode;
    }
    
    public ArrayList<DefaultNode> getSelected() {
        final ArrayList<DefaultNode> nodes = new ArrayList<>();
        final TreePath[] paths = this.tree.getSelectionPaths();
        if (paths == null) {
            return nodes;
        }
        TreePath[] array;
        for (int length = (array = paths).length, i = 0; i < length; ++i) {
            final TreePath tp = array[i];
            final SkidTreeNode selNode = (SkidTreeNode)tp.getLastPathComponent();
            if (selNode.hasNode()) {
                nodes.add(selNode.getNode());
            }
            else if (selNode.hasFileData()) {
                nodes.add(selNode.getFileData());
            }
        }
        return nodes;
    }
    
    @Override
    public void setJarLoaded(final int i) {
        if (i == 1) {
            if (this.lastPath != null) {
                final Enumeration<TreePath> expandedPaths = this.tree.getExpandedDescendants(this.lastPath);
                final TreePath selectedPath = this.tree.getSelectionPath();
                this.tree.setModel(this.makeTree().getModel());
                while (expandedPaths != null && expandedPaths.hasMoreElements()) {
                    final TreePath path = expandedPaths.nextElement();
                    this.tree.expandPath(path);
                }
                this.tree.setSelectionPath(selectedPath);
            }
            else {
                this.lastPath = this.tree.getClosestPathForLocation(0, 0);
                this.tree.setModel(this.makeTree().getModel());
            }
        }
    }
    
    public void setLastPath(final TreePath path) {
        this.lastPath = path;
    }
}
