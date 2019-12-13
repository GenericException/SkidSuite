package me.lpk.gui.tabs;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.event.gui.DropTarget;
import me.lpk.gui.event.gui.LoadJar;
import me.lpk.gui.controls.DropZone;

public class HomeTab extends BasicTab {
	private DropZone dropZone;

	@Override
	protected VerticalBar<Button> createButtonList() {
		Button btnBase = new Button("Load target");
		Button btnCompared = new Button("Load comparison");
		//
		btnBase.setOnAction(new LoadJar(LoadJar.TARGET));
		btnCompared.setOnAction(new LoadJar(LoadJar.COMPARED));
		//
		return new VerticalBar<Button>(1, btnBase, btnCompared);
	}

	@Override
	protected BorderPane createOtherStuff() {
		dropZone = new DropZone("Drag & drop a file to analyze", new DropTarget(this));
		return create(dropZone);
	}

	@Override
	public void targetLoaded() {
	}
}
