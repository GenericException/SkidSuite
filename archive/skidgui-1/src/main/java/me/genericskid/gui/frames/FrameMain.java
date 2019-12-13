package me.genericskid.gui.frames;

import me.genericskid.gui.frames.panel.BasicPanel;

import java.awt.BorderLayout;
import me.genericskid.Main;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import me.genericskid.gui.frames.panel.impl.RelationshipPanel;
import me.genericskid.gui.frames.panel.impl.MappingPanel;
import me.genericskid.gui.frames.panel.impl.AnalyzePanel;
import me.genericskid.gui.frames.panel.impl.PackagePanel;
import me.genericskid.gui.frames.panel.impl.MainPanel;
import java.io.File;
import javax.swing.JFrame;

public class FrameMain extends JFrame
{
    private static final long serialVersionUID = 666L;
    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 400;
    private static File fileBase;
    private static File fileObfu;
    private MainPanel panelMain;
    private PackagePanel panelPackage;
    private AnalyzePanel panelAnalyze;
    private MappingPanel panelMappings;
    private RelationshipPanel panelRelationship;
    public JTabbedPane pane;
    
    public FrameMain() {
        this.init();
    }
    
    private void init() {
        this.panelMain = new MainPanel(this);
        this.panelPackage = new PackagePanel(this);
        this.panelAnalyze = new AnalyzePanel(this);
        this.panelMappings = new MappingPanel(this);
        this.panelRelationship = new RelationshipPanel(this);
        final Dimension frameDimension = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
        final Dimension frameMins = new Dimension(FRAME_WIDTH - 100, FRAME_HEIGHT - 100);
        this.setSize(frameDimension);
        this.setMinimumSize(frameMins);
        this.setDefaultCloseOperation(3);
        this.setTitle("SkidGUI " + Main.VERSION);
        this.setLayout(new BorderLayout());
        (this.pane = new JTabbedPane()).addTab("Main", this.panelMain);
        this.pane.addTab("Analyze", this.panelAnalyze);
        this.pane.addTab("Package Editor", this.panelPackage);
        this.pane.addTab("Relations", this.panelRelationship);
        this.pane.addTab("Mapping Utils", this.panelMappings);
        this.add(this.pane, "Center");
    }
    
    public void setBaseClient(final File file) {
        FrameMain.fileBase = file;
        this.panelMain.setBaseText(file.getPath());
        this.panelMain.setJarLoaded(0);
        this.panelPackage.setJarLoaded(0);
        this.panelMappings.setJarLoaded(0);
        this.panelRelationship.setJarLoaded(0);
    }
    
    public void setObfuscated(final File file) {
        FrameMain.fileObfu = file;
        this.panelMain.setObfuText(file.getPath());
        this.panelMain.setJarLoaded(1);
        this.panelPackage.setJarLoaded(1);
        this.panelMappings.setJarLoaded(1);
        this.panelRelationship.setJarLoaded(1);
    }
    
    public BasicPanel getPanel(final EnumPanel panelType) {
        switch (panelType) {
            case Main: {
                return this.panelMain;
            }
            case Package: {
                return this.panelPackage;
            }
            case Analyze: {
                return this.panelAnalyze;
            }
            case Mapping: {
                return this.panelMappings;
            }
            default: {
                return null;
            }
        }
    }
    
    public static File getFileBase() {
        return FrameMain.fileBase;
    }
    
    public static File getFileObfu() {
        return FrameMain.fileObfu;
    }
}
