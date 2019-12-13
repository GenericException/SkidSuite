package me.lpk.util;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * TODO: Find JavaFX alternative
 */
public class SwingUtil {
	public static JFileChooser makeFileChooser() {
		final JFileChooser temp = new JFileChooser();
		final String dir = System.getProperty("user.dir");
		final File fileDir = new File(dir);
		temp.setCurrentDirectory(fileDir);
		return temp;
	}
}
