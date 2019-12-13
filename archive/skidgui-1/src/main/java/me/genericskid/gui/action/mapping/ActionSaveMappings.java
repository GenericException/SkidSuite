package me.genericskid.gui.action.mapping;

import javax.swing.JFileChooser;

import me.genericskid.util.gui.ComponentUtil;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import me.genericskid.gui.frames.panel.impl.PackagePanel;
import java.awt.event.ActionListener;

public class ActionSaveMappings implements ActionListener
{
    private final PackagePanel instance;
    private final JButton parent;
    
    public ActionSaveMappings(final PackagePanel instance, final JButton parent) {
        this.instance = instance;
        this.parent = parent;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final JFileChooser fc = ComponentUtil.makeFileChooser();
        final int val = fc.showOpenDialog(this.parent);
        if (val == 0) {
            this.instance.saveMappings(fc.getSelectedFile());
        }
    }
}
