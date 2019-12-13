package me.genericskid.gui.action.mapping;

import javax.swing.JFileChooser;

import me.genericskid.util.gui.ComponentUtil;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import me.genericskid.gui.frames.panel.impl.MappingPanel;
import java.awt.event.ActionListener;

public class ActionLoadMapping implements ActionListener
{
    private final MappingPanel instance;
    private final JButton parent;
    private final int index;
    
    public ActionLoadMapping(final MappingPanel instance, final JButton parent, final int index) {
        this.instance = instance;
        this.parent = parent;
        this.index = index;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final JFileChooser fc = ComponentUtil.makeFileChooser();
        final int val = fc.showOpenDialog(this.parent);
        if (val == 0) {
            this.instance.loadMappings(this.index, fc.getSelectedFile());
        }
    }
}
