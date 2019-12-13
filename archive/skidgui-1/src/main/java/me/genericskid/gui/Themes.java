package me.genericskid.gui;

import javax.swing.*;

public class Themes {
	private static final String[] themes = {
			"de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaPlainLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaClassyLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaSilverMoonLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaWhiteVisionLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel",
			"de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel",
	};

	public static void set(int theme) {
		if(theme > 0) {
			try {
				if(theme == 1) {
					UIManager.LookAndFeelInfo[] feels = UIManager.getInstalledLookAndFeels();
					for(int i = 0; i < feels.length; i++) {
						final UIManager.LookAndFeelInfo info = feels[i];
						if(info.getName().equals("Nimbus")) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} else {
					UIManager.setLookAndFeel(themes[theme - 2]);
				}
			} catch(Exception e2) {
				if(theme >= 2)
					System.err.println("Theme not recognized: " + theme);
				else
					e2.printStackTrace();
			}
		}
	}
}
