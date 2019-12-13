package me.genericskid.gui.action.main;

import javax.swing.JFileChooser;

import me.genericskid.util.gui.ComponentUtil;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import me.genericskid.gui.frames.FrameMain;
import java.awt.event.ActionListener;

public class ActionLoadBase implements ActionListener
{
    private final FrameMain instance;
    private final JButton parent;
    
    public ActionLoadBase(final FrameMain instance, final JButton parent) {
        this.instance = instance;
        this.parent = parent;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final JFileChooser fc = ComponentUtil.makeFileChooser();
        final int val = fc.showOpenDialog(this.parent);
        if (val == 0) {
            this.instance.setBaseClient(fc.getSelectedFile());
            System.out.println();
        }
    }
}
