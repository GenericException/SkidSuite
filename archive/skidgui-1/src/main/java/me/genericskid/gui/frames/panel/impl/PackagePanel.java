package me.genericskid.gui.frames.panel.impl;

import me.genericskid.util.node.DefaultNode;
import java.util.Enumeration;
import me.genericskid.util.io.FileIO;
import me.genericskid.util.node.Node;
import me.genericskid.gui.component.SkidTreeNode;
import java.util.ArrayList;
import java.io.File;
import me.genericskid.gui.action.mapping.ActionEditNode;
import me.genericskid.gui.action.mapping.ActionRevertNode;
import me.genericskid.gui.action.mapping.ActionReload;
import me.genericskid.gui.action.mapping.ActionSaveMappings;
import me.genericskid.util.gui.ComponentUtil;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import me.genericskid.gui.frames.FrameMain;
import javax.swing.JButton;
import me.genericskid.gui.frames.panel.TreePanel;

public class PackagePanel extends TreePanel
{
    private static final long serialVersionUID = 1L;

    public PackagePanel(final FrameMain frame) {
        super(frame);
        this.init();
    }
    
    @Override
    protected void init() {
        this.setLayout(new BorderLayout());
        this.pnlButtons = this.makeButtonPanel();
        this.pnlInfo = this.makeInfoPanel();
        (this.splitPane = new JSplitPane(1, this.pnlButtons, this.pnlInfo)).setDividerLocation(150);
        this.splitPane.setDividerSize(5);
        this.splitPane.setEnabled(false);
        this.add(this.splitPane, "Center");
    }
    
    public JPanel makeButtonPanel() {
        final int btnWidth = 149;
        final int btnHeight = 33;
        final boolean padding = false;
        JButton btnEdit = new JButton("Edit node");
        JButton btnRevert = new JButton("Revert to orig");
        JButton btnReload = new JButton("Reload");
        JButton btnSaveMappings = new JButton("Save as mappings");
        btnEdit.setBounds(ComponentUtil.createButtonBounds(0, btnHeight, btnWidth, padding));
        btnRevert.setBounds(ComponentUtil.createButtonBounds(1, btnHeight, btnWidth, padding));
        btnReload.setBounds(ComponentUtil.createButtonBounds(2, btnHeight, btnWidth, padding));
        btnSaveMappings.setBounds(ComponentUtil.createButtonBounds(3, btnHeight, btnWidth, padding));
        btnSaveMappings.addActionListener(new ActionSaveMappings(this, btnSaveMappings));
        btnReload.addActionListener(new ActionReload(this));
        btnRevert.addActionListener(new ActionRevertNode(this));
        btnEdit.addActionListener(new ActionEditNode(this));
        final JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(btnEdit);
        panel.add(btnRevert);
        panel.add(btnReload);
        panel.add(btnSaveMappings);
        return panel;
    }
    
    public void saveMappings(final File file) {
        final ArrayList<String> contents = new ArrayList<>();
        final Object root = this.tree.getModel().getRoot();
        this.tree.getModel().getChildCount(root);
        SkidTreeNode node = (SkidTreeNode)this.tree.getModel().getChild(root, 0);
        final Enumeration<?> en = ((SkidTreeNode)root).preorderEnumeration();
        int count = 0;
        while (en.hasMoreElements()) {
            node = (SkidTreeNode)en.nextElement();
            if (node.getChildCount() == 0) {
                final DefaultNode data = node.getNode();
                Node newNode = new Node(data.getOldName(), data.getParent());
                newNode.setName(data.getNewName());
                final StringBuilder newName = new StringBuilder();
                final StringBuilder oldName = new StringBuilder();
                while (newNode.getParent() != null) {
                    newName.insert(0, newNode.getNewName() + "/");
                    oldName.insert(0, newNode.getOldName() + "/");
                    newNode = newNode.getParent();
                }
                if (!newName.toString().equals(oldName.toString())) {
                    String old = oldName.toString().substring(0, oldName.toString().length() - 1);
                    if (old.endsWith(".class")) {
                        old = old.substring(0, old.length() - 6);
                    }
                    String newS = newName.toString().substring(0, newName.toString().length() - 1);
                    if (newS.endsWith(".class")) {
                        newS = newS.substring(0, newS.length() - 6);
                    }
                    contents.add("CLASS " + old + " " + newS);
                }
            }
            if (++count > 10000) {
                System.out.println("Aborting!");
                break;
            }
        }
        System.out.println("Saving - " + contents.size());
        FileIO.saveAllLines(file.getAbsolutePath(), contents);
    }
}
