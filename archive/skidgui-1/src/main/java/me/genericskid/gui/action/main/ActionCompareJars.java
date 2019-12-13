package me.genericskid.gui.action.main;

import java.awt.Desktop;
import me.genericskid.util.io.FileIO;
import java.io.File;
import me.genericskid.util.signatures.Signature;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.IOException;
import me.genericskid.util.signatures.SignatureUtil;
import me.genericskid.util.signatures.SignatureRules;
import me.genericskid.gui.frames.EnumPanel;
import me.genericskid.gui.frames.panel.impl.MainPanel;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.FrameMain;
import java.awt.event.ActionListener;

public class ActionCompareJars implements ActionListener
{
    private final FrameMain instance;
    
    public ActionCompareJars(final FrameMain instance) {
        this.instance = instance;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        ArrayList<Signature> origSigs = null;
        ArrayList<Signature> obfuSigs = null;
        final File jarOrig = FrameMain.getFileBase();
        final File jarObfu = FrameMain.getFileObfu();
        if (jarOrig != null && jarObfu != null && jarOrig.exists() && jarObfu.exists()) {
            final MainPanel mp = (MainPanel)this.instance.getPanel(EnumPanel.Main);
            try {
                final SignatureRules rulesOrig = new SignatureRules(mp.getLimitingPackagesBase(), mp.getPackagesBase(), mp.ignoreZKMBase(), mp.hasObfIDsBase(), mp.checkWithCL());
                final SignatureRules rulesObfu = new SignatureRules(mp.getLimitingPackagesObfu(), mp.getPackagesObfu(), mp.ignoreZKMObfu(), mp.hasObfIDsObfu(), mp.checkWithCL());
                origSigs = SignatureUtil.populateSigs(jarOrig.getAbsolutePath(), rulesOrig);
                obfuSigs = SignatureUtil.populateSigs(jarObfu.getAbsolutePath(), rulesObfu);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (origSigs != null && obfuSigs != null) {
                SignatureUtil.compareSigs2(origSigs, obfuSigs, jarObfu.getAbsolutePath(), mp.findMethodsAndFields(), jarOrig.getAbsolutePath(), mp.checkWithCL(), mp.isMethodFieldSearchSafe());
            }
            else {
                JOptionPane.showMessageDialog(null, "Signature list is null! (Aborted)");
            }
            if (mp.doSaveSigs()) {
                this.saveSigs(jarOrig.getName() + "-Signatures.txt", origSigs);
                this.saveSigs(jarObfu.getName() + "-Signatures.txt", obfuSigs);
            }
        }
    }
    
    private void saveSigs(final String file, final ArrayList<Signature> list) {
        final ArrayList<String> sigStrs = new ArrayList<>();
        for (final Signature s : list) {
            sigStrs.add(s.getOwner() + "-" + s.toSig());
        }
        FileIO.saveAllLines(file, sigStrs);
        try {
            final Desktop d = Desktop.getDesktop();
            d.open(new File(file));
        }
        catch (Exception ex) {}
    }
}
