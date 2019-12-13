package me.genericskid.gui.frames.panel.impl;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import me.genericskid.gui.action.mapping.ActionMergeMapping;
import java.awt.event.ActionListener;
import me.genericskid.gui.action.mapping.ActionLoadMapping;
import me.genericskid.util.gui.ComponentUtil;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import me.genericskid.gui.frames.FrameMain;
import me.genericskid.util.mapping.Mappings;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import me.genericskid.gui.frames.panel.BasicPanel;

public class MappingPanel extends BasicPanel
{
    private static final long serialVersionUID = 1L;
    private JButton btnMerge;
    private JRadioButton rad1on2;
    private JRadioButton rad2on1;
    private JRadioButton radLetMe;
    private JCheckBox chkSanitize;
    private final ArrayList<Mappings> mappings1;
    private final ArrayList<Mappings> mappings2;
    private boolean b0;

    public MappingPanel(final FrameMain frameMain) {
        super(frameMain);
        this.mappings1 = new ArrayList<>();
        this.mappings2 = new ArrayList<>();
        this.init();
    }
    
    @Override
    protected void init() {
        this.setLayout(new BorderLayout());
        final JPanel pnlButtons = this.makeButtonPanel();
        final JPanel pnlInfo = this.makeInfoPanel();
        (this.splitPane = new JSplitPane(1, pnlButtons, pnlInfo)).setDividerLocation(150);
        this.splitPane.setDividerSize(5);
        this.splitPane.setEnabled(false);
        this.add(this.splitPane, "Center");
    }
    
    @Override
    protected JPanel makeButtonPanel() {
        final int btnWidth = 149;
        final int btnHeight = 33;
        final boolean padding = false;
        final JPanel panel = new JPanel();
        panel.setLayout(null);
        JButton btnLoadMapping1 = new JButton("Load Mapping 1");
        JButton btnLoadMapping2 = new JButton("Load Mapping 2");
        (this.btnMerge = new JButton("Merge Mappings")).setToolTipText("Only exports class names. Field and methods will be lost.");
        btnLoadMapping1.setBounds(ComponentUtil.createButtonBounds(0, btnHeight, btnWidth, padding));
        btnLoadMapping2.setBounds(ComponentUtil.createButtonBounds(1, btnHeight, btnWidth, padding));
        this.btnMerge.setBounds(ComponentUtil.createButtonBounds(2, btnHeight, btnWidth, padding));
        btnLoadMapping1.addActionListener(new ActionLoadMapping(this, btnLoadMapping1, 0));
        btnLoadMapping2.addActionListener(new ActionLoadMapping(this, btnLoadMapping2, 1));
        this.btnMerge.addActionListener(new ActionMergeMapping(this));
        this.btnMerge.setEnabled(false);
        panel.add(btnLoadMapping1);
        panel.add(btnLoadMapping2);
        panel.add(this.btnMerge);
        return panel;
    }
    
    @Override
    protected JPanel makeInfoPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 3));
        this.rad1on2 = new JRadioButton("Merge 1 onto 2");
        this.rad2on1 = new JRadioButton("Merge 2 onto 1");
        this.radLetMe = new JRadioButton("Let me choose for each case");
        (this.chkSanitize = new JCheckBox("Sanitize output")).setSelected(true);
        this.rad1on2.setSelected(true);
        this.rad1on2.addActionListener(arg0 -> {
            MappingPanel.this.rad2on1.setSelected(false);
            MappingPanel.this.radLetMe.setSelected(false);
        });
        this.rad2on1.addActionListener(arg0 -> {
            MappingPanel.this.rad1on2.setSelected(false);
            MappingPanel.this.radLetMe.setSelected(false);
        });
        this.radLetMe.addActionListener(arg0 -> {
            MappingPanel.this.rad1on2.setSelected(false);
            MappingPanel.this.rad2on1.setSelected(false);
        });
        panel.add(this.rad1on2);
        panel.add(this.rad2on1);
        panel.add(this.radLetMe);
        panel.add(this.chkSanitize);
        return panel;
    }
    
    public void loadMappings(final int index, final File file) {
        ArrayList<Mappings> maps = null;
        if (index == 0) {
            maps = this.mappings1;
        }
        else {
            maps = this.mappings2;
        }
        maps.clear();
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                final String pre = "CLASS ";
                if (line.startsWith(pre)) {
                    line = line.substring(pre.length());
                    final String[] split = line.split(" ");
                    if (split.length <= 1) {
                        continue;
                    }
                    maps.add(new Mappings(split[0], split[1]));
                }
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (this.mappings1.size() > 0 && this.mappings2.size() > 0) {
            this.btnMerge.setEnabled(true);
        }
    }
    
    public ArrayList<Mappings> getMappings(final int i) {
        return (i == 0) ? this.mappings1 : this.mappings2;
    }
    
    public int getMergeMode() {
        return this.rad1on2.isSelected() ? 0 : (this.rad2on1.isSelected() ? 1 : 2);
    }
    
    public boolean getSanitization() {
        return this.chkSanitize.isSelected();
    }
    
    @Override
    public void setJarLoaded(final int i) {
        if (i == 0) {
            this.b0 = true;
        }
        else {
            boolean b1 = true;
        }
        if (this.b0) {}
    }
}
