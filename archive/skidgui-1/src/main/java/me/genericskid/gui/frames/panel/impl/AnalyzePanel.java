package me.genericskid.gui.frames.panel.impl;

import me.genericskid.util.signatures.SignatureUtil;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;

import me.genericskid.gui.action.analysis.ActionSearchMalware;
import me.genericskid.gui.action.analysis.ActionSearchStrings;
import me.genericskid.util.gui.ComponentUtil;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import me.genericskid.gui.frames.FrameMain;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import me.genericskid.gui.frames.panel.BasicPanel;

public class AnalyzePanel extends BasicPanel
{
    private static final long serialVersionUID = 1L;
    private JTextArea txtPackages;
    private JTextArea txtMalSigs;
    private JTextArea txtSearchStrings;
    private JCheckBox chkInclusive;
    private JCheckBox chkExclusive;

    public AnalyzePanel(final FrameMain frame) {
        super(frame);
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
        JButton btnSearchStrings = new JButton("Search strings");
        JButton btnMalware;
        (btnMalware = new JButton("Check suspicious")).setToolTipText(
                "Looks for: \nFile IO\nNetorking\nReflection+Classloading\nURLs\nAnd user-given signatures");
        JButton btnLoadMalSigs = new JButton("Load malware sigs");
        btnSearchStrings.setBounds(ComponentUtil.createButtonBounds(0, btnHeight, btnWidth, padding));
        btnMalware.setBounds(ComponentUtil.createButtonBounds(1, btnHeight, btnWidth, padding));
        btnLoadMalSigs.setBounds(ComponentUtil.createButtonBounds(2, btnHeight, btnWidth, padding));
        final ActionSearchStrings actionSearchStrings = new ActionSearchStrings(this.frame);
        final ActionSearchMalware actionSearchMalware = new ActionSearchMalware(this.frame);
        btnSearchStrings.addActionListener(actionSearchStrings);
        btnMalware.addActionListener(actionSearchMalware);
        panel.add(btnSearchStrings);
        panel.add(btnMalware);
        return panel;
    }
    
    @Override
    protected JPanel makeInfoPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 3));
        this.txtSearchStrings = new JTextArea("Search for strings (Separate by line)");
        this.txtPackages = new JTextArea("net.minecraft\ncom.mojang");
        this.txtMalSigs = new JTextArea("Copy malicious signatures here\n\n2:1:1:3:0:8:8:11:7:3:54:2\n1:1:1:0:0:2:2:18:1:0:0:0");
        this.chkInclusive = new JCheckBox("Include results in packages");
        this.chkExclusive = new JCheckBox("Exclude results in packages");
        final JScrollPane scrPane1 = new JScrollPane(this.txtSearchStrings);
        final JScrollPane scrPane2 = new JScrollPane(this.txtPackages);
        final JScrollPane scrPane3 = new JScrollPane(this.txtMalSigs);
        final JSplitPane splitMain = new JSplitPane();
        final JSplitPane splitSub = new JSplitPane();
        final JSplitPane splitPackages = new JSplitPane();
        final JPanel pnlPackages = new JPanel();
        pnlPackages.setLayout(new BoxLayout(pnlPackages, 3));
        pnlPackages.add(this.chkExclusive);
        pnlPackages.add(this.chkInclusive);
        splitSub.setOrientation(0);
        splitMain.setOrientation(0);
        splitSub.setTopComponent(splitPackages);
        splitSub.setBottomComponent(scrPane3);
        splitPackages.setDividerLocation(120);
        splitMain.setTopComponent(scrPane1);
        splitMain.setBottomComponent(splitSub);
        splitMain.setDividerLocation(55);
        splitPackages.setLeftComponent(scrPane2);
        splitPackages.setRightComponent(pnlPackages);
        panel.add(splitMain);
        return panel;
    }
    
    public String[] getSearchText() {
        return this.txtSearchStrings.getText().contains("\n") ? this.txtSearchStrings.getText().split("\n") : new String[] { this.txtSearchStrings.getText() };
    }
    
    public String[] getPackages() {
        return this.txtPackages.getText().contains("\n") ? this.txtPackages.getText().split("\n") : new String[] { this.txtPackages.getText() };
    }
    
    public boolean isInclusive() {
        return this.chkInclusive.isSelected();
    }
    
    public boolean isExclusive() {
        return this.chkExclusive.isSelected();
    }
    
    public ArrayList<String> getMalSigs() {
        final ArrayList<String> sigs = new ArrayList<>();
        String[] split;
        for (int length = (split = this.txtMalSigs.getText().split("\n")).length, i = 0; i < length; ++i) {
            final String s = split[i];
            if (SignatureUtil.isSig(s)) {
                sigs.add(s);
            }
        }
        return sigs;
    }
    
    public boolean isCaseSensitive() {
        return false;
    }
    
    @Override
    public void setJarLoaded(final int i) {
    }
}
