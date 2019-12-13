package me.genericskid.gui.action.mapping;

import java.util.ArrayList;
import me.genericskid.util.node.DefaultNode;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.panel.impl.PackagePanel;
import java.awt.event.ActionListener;

public class ActionRevertNode implements ActionListener
{
    private final PackagePanel instance;
    
    public ActionRevertNode(final PackagePanel instance) {
        this.instance = instance;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final ArrayList<DefaultNode> nodes = this.instance.getSelected();
        for (final DefaultNode n : nodes) {
            n.setName(n.getOldName());
        }
    }
}
