package me.lpk.gui.controls;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class FitSplitPlane extends SplitPane {
	@Override
	protected void layoutChildren() {
		// Only used in one situation.
		// Will bother to make width modifiable if necessary.
		double width = (200 / getWidth());
		setDividerPosition(0, width);
		for (Node node : getChildren()) {
			setResizableWithParent(node, false);
		}
		super.layoutChildren();
	}
}
