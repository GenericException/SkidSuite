package me.lpk.gui.controls;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Aligns a group of controls vertically, giving each one equal space.
 *
 * @param <T>
 *            Objects to align vertically
 */
public class VerticalBar<T extends Control> extends VBox {
	@SafeVarargs
	public VerticalBar(double spacing, T... items) {
		super(spacing);
		getChildren().addAll(items);
		for (Control b : items) {
			HBox.setHgrow(b, Priority.ALWAYS);
			b.setMaxWidth(Double.MAX_VALUE);
		}
	}

	@Override
	protected void layoutChildren() {
		double minPrefWidth = 0;
		for (Node n : getChildren()) {
			minPrefWidth = Math.max(minPrefWidth, n.prefWidth(-1));
		}
		for (Node n : getChildren()) {
			if (n instanceof Control) {
				((Control) n).setMinWidth(minPrefWidth);
			}
		}
		super.layoutChildren();
	}

	public void add(Node item) {
		HBox.setHgrow(item, Priority.ALWAYS);
		if (item instanceof Control)
			((Control) item).setMaxWidth(Double.MAX_VALUE);
		ObservableList<Node> buttons = getChildren();
		if (!buttons.contains(item)) {
			buttons.add(item);
		}
	}

	public void remove(Node item) {
		getChildren().remove(item);
	}
}