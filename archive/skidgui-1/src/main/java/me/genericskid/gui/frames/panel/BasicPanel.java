package me.genericskid.gui.frames.panel;

import javax.swing.JSplitPane;
import me.genericskid.gui.frames.FrameMain;
import javax.swing.JPanel;

public abstract class BasicPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    public final FrameMain frame;
    protected JSplitPane splitPane;
    protected JPanel pnlInfo;
    protected JPanel pnlButtons;
    
    public BasicPanel(final FrameMain frame) {
        this.frame = frame;
        this.init();
    }
    
    protected abstract void init();
    
    protected abstract JPanel makeButtonPanel();
    
    protected abstract JPanel makeInfoPanel();
    
    public abstract void setJarLoaded(final int p0);
}
