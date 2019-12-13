package me.lpk.gui.event.gui;

import java.io.File;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import me.lpk.gui.Main;
import me.lpk.gui.tabs.HomeTab;

public class DropTarget implements EventHandler<DragEvent> {
	@SuppressWarnings("unused")
	private final HomeTab tab;

	public DropTarget(HomeTab tab) {
		this.tab = tab;
	}

	@Override
	public void handle(DragEvent event) {
		boolean success = false;
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			int size = db.getFiles().size();
			for (int i = 0; i < size; i++) {
				File file = db.getFiles().get(i);
				if (!file.getAbsolutePath().endsWith(".jar")) {
					continue;
				}
				success = true;
				if (i == 0) {
					Main.setTargetJar(file);
				} else if (i == 1) {
					Main.setComparisonJar(file);
				}
			}
		}
		if (success) {
			// TODO: Make a cool information display on the home tab
			// tab.updateDisplay();
		}
		event.setDropCompleted(success);
		event.consume();
	}

}
