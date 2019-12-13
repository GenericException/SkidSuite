package me.lpk.gui.event.gui;

import java.io.File;
import javax.swing.JFileChooser;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import me.lpk.gui.Main;
import me.lpk.util.SwingUtil;

public class LoadJar implements EventHandler<ActionEvent> {
	public static final int COMPARED = 0, TARGET = 1;
	private final int jar;

	public LoadJar(int jar) {
		this.jar = jar;
	}

	@Override
	public void handle(ActionEvent e) {
		JFileChooser fc = SwingUtil.makeFileChooser();
		int val = fc.showOpenDialog(null);
		if (val == JFileChooser.APPROVE_OPTION) {
			File jarFile = fc.getSelectedFile();
			if (jar == COMPARED) {
				Main.setComparisonJar(jarFile);
			} else if (jar == TARGET) {
				Main.setTargetJar(jarFile);
			}
		}

	}
}
