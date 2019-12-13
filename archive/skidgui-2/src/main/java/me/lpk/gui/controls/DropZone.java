package me.lpk.gui.controls;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.TextAlignment;

/**
 * A plane that accepts via drag and drop functionality.
 */
public class DropZone extends Label {
	/**
	 * @param text
	 *            Text displayed
	 * @param dropHandler
	 *            Handler for when the file is accepted
	 */
	public DropZone(String text, EventHandler<DragEvent> dropHandler) {
		setOnDragOver(new DragOver());
		setOnDragDropped(dropHandler);
		setText(text);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		setAlignment(Pos.CENTER);
		setTextAlignment(TextAlignment.CENTER);
	}
}

class DragOver implements EventHandler<DragEvent> {
	@Override
	public void handle(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			event.acceptTransferModes(TransferMode.ANY);
		} else {
			event.consume();
		}
	}
}