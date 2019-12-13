package me.genericskid.gui.frames.panel.impl;

import me.genericskid.gui.action.drag.ActionShowChildren;
import me.genericskid.gui.action.drag.ActionShowParents;
import me.genericskid.util.gui.ComponentUtil;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import me.genericskid.gui.frames.FrameMain;
import javax.swing.JButton;
import me.genericskid.gui.frames.panel.TreePanel;

public class RelationshipPanel extends TreePanel
{
    private static final long serialVersionUID = 1L;
    private JButton btnParents;
    private JButton btnChildren;
    
    public RelationshipPanel(final FrameMain frame) {
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
        this.btnParents = new JButton("Show parents");
        this.btnChildren = new JButton("Show children");
        this.btnParents.setEnabled(false);
        this.btnChildren.setEnabled(false);
        this.btnParents.setBounds(ComponentUtil.createButtonBounds(0, btnHeight, btnWidth, padding));
        this.btnChildren.setBounds(ComponentUtil.createButtonBounds(1, btnHeight, btnWidth, padding));
        this.btnParents.addActionListener(new ActionShowParents(this));
        this.btnChildren.addActionListener(new ActionShowChildren(this));
        final JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(this.btnParents);
        panel.add(this.btnChildren);
        return panel;
    }
    
    @Override
    public void setJarLoaded(final int i) {
        super.setJarLoaded(i);
        if (i == 1) {
            this.btnParents.setEnabled(true);
            this.btnChildren.setEnabled(true);
        }
    }
}
