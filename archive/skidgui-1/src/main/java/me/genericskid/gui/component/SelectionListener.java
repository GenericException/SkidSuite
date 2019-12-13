package me.genericskid.gui.component;

import javax.swing.event.TreeSelectionEvent;
import me.genericskid.gui.frames.panel.TreePanel;
import javax.swing.event.TreeSelectionListener;

public class SelectionListener implements TreeSelectionListener
{
    private final TreePanel panel;
    
    public SelectionListener(final TreePanel panelTreeView) {
        this.panel = panelTreeView;
    }
    
    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        this.panel.setLastPath(e.getPath());
    }
}
