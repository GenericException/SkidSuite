package me.genericskid.gui.action.main;

import java.io.File;
import net.contra.jmd.transformers.zkm.ZKMTransformer;
import me.genericskid.util.signatures.SignatureRules;
import me.genericskid.gui.frames.EnumPanel;
import me.genericskid.gui.frames.panel.impl.MainPanel;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.FrameMain;
import java.awt.event.ActionListener;

public class ActionFixZKM implements ActionListener
{
    private final FrameMain instance;
    
    public ActionFixZKM(final FrameMain instance) {
        this.instance = instance;
    }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        try {
            final File file = FrameMain.getFileObfu();
            if(file != null && file.exists()) {
                final MainPanel mp = (MainPanel) this.instance.getPanel(EnumPanel.Main);
                final SignatureRules rulesObfu = new SignatureRules(mp.getLimitingPackagesObfu(), mp.getPackagesObfu(), mp.ignoreZKMObfu(), mp.hasObfIDsObfu(), mp.checkWithCL());
                final ZKMTransformer zt = new ZKMTransformer(file.getAbsolutePath(), rulesObfu);
                zt.transform();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
