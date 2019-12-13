package me.genericskid.gui.action.mapping;

import java.io.File;
import java.awt.Desktop;
import me.genericskid.util.io.FileIO;
import javax.swing.JOptionPane;
import me.genericskid.util.mapping.Mappings;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.panel.impl.MappingPanel;
import java.awt.event.ActionListener;

public class ActionMergeMapping implements ActionListener
{
    private final MappingPanel panel;
    
    public ActionMergeMapping(final MappingPanel panel) {
        this.panel = panel;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final ArrayList<Mappings> ml1 = this.panel.getMappings(0);
        final ArrayList<Mappings> ml2 = this.panel.getMappings(1);
        final ArrayList<Mappings> out = new ArrayList<>();
        final int mergeMode = this.panel.getMergeMode();
        for (final Mappings m1 : ml1) {
            for (final Mappings m2 : ml2) {
                final boolean match = m1.getOriginalName().equals(m2.getOriginalName());
                if (match) {
                    if (mergeMode == 0) {
                        this.addCheck(out, new Mappings(m1.getOriginalName(), m1.getNewName()));
                    }
                    else if (mergeMode == 1) {
                        this.addCheck(out, new Mappings(m1.getOriginalName(), m2.getNewName()));
                    }
                    else {
                        if (mergeMode != 2) {
                            continue;
                        }
                        String input;
                        for (input = null; input == null || input.isEmpty(); input = null) {
                            input = JOptionPane.showInputDialog("For the class: " + m1.getOriginalName() + "\n\tMapping 1: " + m1.getNewName() + "\n" + "\n\tMapping 2: " + m2.getNewName() + "\n" + "Which mapping should be used? (Enter 1 or 2)");
                            if (!this.isNumeric(input)) {}
                        }
                        final int choice = Integer.parseInt(input);
                        if (choice == 1) {
                            this.addCheck(out, new Mappings(m1.getOriginalName(), m1.getNewName()));
                        }
                        else {
                            this.addCheck(out, new Mappings(m1.getOriginalName(), m2.getNewName()));
                        }
                    }
                }
            }
        }
        for (final Mappings m1 : ml1) {
            if (!out.contains(m1)) {
                this.addCheck(out, m1);
            }
        }
        for (final Mappings m3 : ml2) {
            if (!out.contains(m3)) {
                this.addCheck(out, m3);
            }
        }
        final ArrayList<String> outs = new ArrayList<>();
        for (final Mappings i : out) {
            outs.add(i.toMapping());
        }
        final String fileName = "MergedMappings.mapping";
        FileIO.saveAllLines("MergedMappings.mapping", outs);
        try {
            final Desktop d = Desktop.getDesktop();
            d.open(new File(fileName));
        }
        catch (Exception ex) {}
    }
    
    private void addCheck(final ArrayList<Mappings> list, final Mappings m) {
        if (this.panel.getSanitization()) {
            if (list.contains(m)) {
                return;
            }
            for (final Mappings listM : list) {
                if (listM.getNewName().equals(m.getNewName())) {
                    return;
                }
                if (listM.getOriginalName().equals(m.getOriginalName())) {
                    return;
                }
            }
            list.add(m);
        }
        else {
            list.add(m);
        }
    }
    
    private boolean isNumeric(final String input) {
        try {
            final int i = Integer.parseInt(input);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
