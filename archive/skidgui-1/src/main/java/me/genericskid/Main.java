package me.genericskid;

import me.genericskid.gui.Themes;
import me.genericskid.gui.frames.FrameMain;
import me.genericskid.util.io.ConfigFile;

public class Main {
	public static String VERSION;
	public static final ConfigFile config;

	static {
		Main.VERSION = "1.7";
		config = new ConfigFile("skid.config");
	}

	public static void main(final String[] args) {
		Themes.set(Main.config.getTheme());
		FrameMain mf = new FrameMain();
		mf.setVisible(true);
	}
}
