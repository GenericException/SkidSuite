package me.genericskid.gui.action.mapping;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import me.genericskid.util.node.DefaultNode;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.panel.impl.PackagePanel;
import java.awt.event.ActionListener;

public class ActionEditNode implements ActionListener
{
    private final PackagePanel instance;
    
    public ActionEditNode(final PackagePanel instance) {
        this.instance = instance;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final ArrayList<DefaultNode> nodes = this.instance.getSelected();
        for (final DefaultNode n : nodes) {
            String newName = null;
            for (boolean isFile = n.getChildren().size() == 0; newName == null || newName.isEmpty(); newName = JOptionPane.showInputDialog("Give a new name for the " + (isFile ? "file" : "package") + ": ", n.getNewName())) {}
            n.setName(newName);
        }
    }
}
