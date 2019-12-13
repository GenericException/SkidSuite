package me.lpk.gui.tabs;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.controls.FitSplitPlane;

public abstract class BasicTab extends BorderPane {
	protected VerticalBar<?> controlBar;
	protected BorderPane otherControls;
	private final SplitPane pane = new FitSplitPlane();

	public void setup() {
		pane.getItems().addAll(controlBar = createButtonList(), otherControls = createOtherStuff());
		setCenter(pane);
	}

	protected abstract VerticalBar<?> createButtonList();

	protected abstract BorderPane createOtherStuff();

	public abstract void targetLoaded();

	protected final BorderPane create(Node node) {
		BorderPane pane = new BorderPane();
		pane.setCenter(node);
		return pane;
	}

	public VerticalBar<?> getControlBar() {
		return controlBar;
	}

	public BorderPane getOtherControls() {
		return otherControls;
	}

	public void update() {
		pane.getItems().clear();
		pane.getItems().addAll(controlBar, otherControls);
	}
}
