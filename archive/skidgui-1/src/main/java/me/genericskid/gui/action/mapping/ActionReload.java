package me.genericskid.gui.action.mapping;

import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.panel.impl.PackagePanel;
import java.awt.event.ActionListener;

public class ActionReload implements ActionListener
{
    private final PackagePanel instance;
    
    public ActionReload(final PackagePanel instance) {
        this.instance = instance;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        this.instance.setJarLoaded(1);
    }
}
