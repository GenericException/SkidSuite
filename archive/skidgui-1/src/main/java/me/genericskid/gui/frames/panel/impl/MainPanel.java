package me.genericskid.gui.frames.panel.impl;

import java.awt.Dimension;
import javax.swing.BoxLayout;

import me.genericskid.gui.action.main.ActionUnpackJCrypt;
import me.genericskid.gui.action.main.ActionFixZKM;
import me.genericskid.gui.action.main.ActionCompareJars;
import me.genericskid.gui.action.main.ActionLoadObfu;
import me.genericskid.gui.action.main.ActionLoadBase;
import me.genericskid.util.gui.ComponentUtil;

import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import me.genericskid.gui.frames.FrameMain;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.genericskid.gui.frames.panel.BasicPanel;

public class MainPanel extends BasicPanel
{
    private static final long serialVersionUID = 1L;
    private static final String LABEL_BASE_TEXT = "Base JAR:";
    private static final String LABEL_OBFU_TEXT = "Obfuscated JAR:";
    private boolean jarCheck0;
    private boolean jarCheck1;
    private JTextField txtBase;
    private JTextField txtObfu;
    private JTextField txtPackagesBase;
    private JTextField txtPackagesObfu;
    private JButton btnCompareJars;
    private JButton btnFixZMK;
    private JButton btnDumpJcrypt;
    private JCheckBox chkObfu_ZKM;
    private JCheckBox chkObfu_Packages;
    private JCheckBox chkObfu_ObfIDs;
    private JCheckBox chkBase_ZKM;
    private JCheckBox chkBase_Packages;
    private JCheckBox chkBase_ObfIDs;
    private JCheckBox chkSaveSigs;
    private JCheckBox chkFieldsMethods;
    private JCheckBox chkFMSafe;
    private JCheckBox chkCompareCL;
    
    public MainPanel(final FrameMain frame) {
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
        JButton btnLoadBase = new JButton("Load base");
        JButton btnLoadObfu = new JButton("Load obfuscated");
        this.btnCompareJars = new JButton("Compare JARs");
        this.btnFixZMK = new JButton("Fix ZKM strings");
        this.btnDumpJcrypt = new JButton("Dump JCrypt JAR");
        btnLoadBase.setToolTipText("Load a jar file to be used as a base comparison");
        btnLoadObfu.setToolTipText("Load a jar file to be analyzed.");
        this.btnCompareJars.setToolTipText("Generate a mappings file for the obfuscated jar based on the non-obfuscated jar.");
        this.btnFixZMK.setToolTipText("Replace ZKM-encrypted strings in the obfuscated jar with normal strings");
        this.btnDumpJcrypt.setToolTipText("Unpack the dat file in JCrypt-protected jars.");
        this.btnCompareJars.setEnabled(false);
        this.btnFixZMK.setEnabled(false);
        this.btnDumpJcrypt.setEnabled(false);
        btnLoadBase.setBounds(ComponentUtil.createButtonBounds(0, btnHeight, btnWidth, padding));
        btnLoadObfu.setBounds(ComponentUtil.createButtonBounds(1, btnHeight, btnWidth, padding));
        this.btnCompareJars.setBounds(ComponentUtil.createButtonBounds(2, btnHeight, btnWidth, padding));
        this.btnFixZMK.setBounds(ComponentUtil.createButtonBounds(3, btnHeight, btnWidth, padding));
        this.btnDumpJcrypt.setBounds(ComponentUtil.createButtonBounds(4, btnHeight, btnWidth, padding));
        final ActionLoadBase actionLoadBase = new ActionLoadBase(this.frame, btnLoadBase);
        final ActionLoadObfu actionLoadObfu = new ActionLoadObfu(this.frame, btnLoadObfu);
        final ActionCompareJars actionCompareJars = new ActionCompareJars(this.frame);
        final ActionFixZKM actionFixZKMStrings = new ActionFixZKM(this.frame);
        final ActionUnpackJCrypt actionUnpackJC = new ActionUnpackJCrypt(this.frame);
        btnLoadBase.addActionListener(actionLoadBase);
        btnLoadObfu.addActionListener(actionLoadObfu);
        this.btnCompareJars.addActionListener(actionCompareJars);
        this.btnFixZMK.addActionListener(actionFixZKMStrings);
        this.btnDumpJcrypt.addActionListener(actionUnpackJC);
        panel.add(btnLoadBase);
        panel.add(btnLoadObfu);
        panel.add(this.btnCompareJars);
        panel.add(this.btnFixZMK);
        panel.add(this.btnDumpJcrypt);
        return panel;
    }
    
    @Override
    protected JPanel makeInfoPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 3));
        this.txtBase = new JTextField("Base JAR:");
        this.txtObfu = new JTextField("Obfuscated JAR:");
        this.txtPackagesBase = new JTextField("net.minecraft,com.example");
        this.txtPackagesObfu = new JTextField("net.minecraft,com.example");
        JPanel pnlBase = new JPanel();
        JPanel pnlObfu = new JPanel();
        this.chkBase_ZKM = new JCheckBox("Ignore ZKM fields");
        this.chkObfu_ZKM = new JCheckBox("Ignore ZKM fields");
        this.chkBase_Packages = new JCheckBox("Only use listed packages");
        this.chkObfu_Packages = new JCheckBox("Only use listed packages");
        this.chkBase_ObfIDs = new JCheckBox("Client has obf ID's from MCP");
        this.chkObfu_ObfIDs = new JCheckBox("Client has obf ID's from MCP");
        this.txtBase.setEditable(false);
        this.txtObfu.setEditable(false);
        pnlBase.setLayout(new BoxLayout(pnlBase, 3));
        pnlObfu.setLayout(new BoxLayout(pnlObfu, 3));
        this.txtBase.setMaximumSize(new Dimension(2000, 33));
        this.txtObfu.setMaximumSize(new Dimension(2000, 33));
        this.txtPackagesBase.setMaximumSize(new Dimension(2000, 33));
        this.txtPackagesObfu.setMaximumSize(new Dimension(2000, 33));
        this.chkSaveSigs = new JCheckBox("Save signatures when comparing");
        this.chkFieldsMethods = new JCheckBox("Make mappings for fields and methods");
        this.chkFMSafe = new JCheckBox("Remove possible duplicate field/method mappings");
        this.chkCompareCL = new JCheckBox("Compare using MCP CL IDs");
        this.chkFMSafe.setSelected(true);
        this.chkFieldsMethods.setSelected(true);
        pnlBase.add(this.txtBase);
        pnlBase.add(this.chkBase_ZKM);
        pnlBase.add(this.chkBase_ObfIDs);
        pnlBase.add(this.chkBase_Packages);
        pnlBase.add(this.txtPackagesBase);
        pnlObfu.add(this.txtObfu);
        pnlObfu.add(this.chkObfu_ZKM);
        pnlObfu.add(this.chkObfu_ObfIDs);
        pnlObfu.add(this.chkObfu_Packages);
        pnlObfu.add(this.txtPackagesObfu);
        panel.add(pnlBase, "Base");
        panel.add(pnlObfu, "Obfuscated");
        panel.add(this.chkSaveSigs);
        panel.add(this.chkFieldsMethods);
        panel.add(this.chkFMSafe);
        return panel;
    }
    
    @Override
    public void setJarLoaded(final int i) {
        if (i == 0) {
            this.jarCheck0 = true;
        }
        else if (i == 1) {
            this.jarCheck1 = true;
            this.btnFixZMK.setEnabled(true);
            this.btnDumpJcrypt.setEnabled(true);
        }
        if (this.jarCheck0 && this.jarCheck1) {
            this.btnCompareJars.setEnabled(true);
        }
    }
    
    public boolean ignoreZKMBase() {
        return this.chkBase_ZKM.isSelected();
    }
    
    public boolean ignoreZKMObfu() {
        return this.chkObfu_ZKM.isSelected();
    }
    
    public boolean getLimitingPackagesBase() {
        return this.chkBase_Packages.isSelected();
    }
    
    public boolean getLimitingPackagesObfu() {
        return this.chkObfu_Packages.isSelected();
    }
    
    public boolean hasObfIDsBase() {
        return this.chkBase_ObfIDs.isSelected();
    }
    
    public boolean hasObfIDsObfu() {
        return this.chkObfu_ObfIDs.isSelected();
    }
    
    public boolean doSaveSigs() {
        return this.chkSaveSigs.isSelected();
    }
    
    public String[] getPackagesBase() {
        final String text = this.txtPackagesBase.getText();
        if (text.contains(",")) {
            return text.split(",");
        }
        return new String[] { text };
    }
    
    public String[] getPackagesObfu() {
        final String text = this.txtPackagesObfu.getText();
        if (text.contains(",")) {
            return text.split(",");
        }
        return new String[] { text };
    }
    
    public void setObfuText(final String s) {
        this.txtObfu.setText("Obfuscated JAR: " + s);
    }
    
    public void setBaseText(final String s) {
        this.txtBase.setText("Base JAR: " + s);
    }
    
    public boolean findMethodsAndFields() {
        return this.chkFieldsMethods.isSelected();
    }
    
    public boolean isMethodFieldSearchSafe() {
        return this.chkFMSafe.isSelected();
    }
    
    public boolean checkWithCL() {
        return this.chkCompareCL.isSelected();
    }
}
