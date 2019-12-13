package me.lpk.gui.tabs;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.tabs.internal.InternalTab;
import me.lpk.gui.tabs.internal.InternalBytecode;
import me.lpk.gui.tabs.internal.InternalObfuPatcher;
import me.lpk.gui.tabs.internal.InternalStack;
import me.lpk.gui.tabs.internal.InternalStringPatch;

public class PatchingTab extends BasicTab {
	private final InternalStringPatch intStringPatch = new InternalStringPatch();
	private final InternalObfuPatcher intObfuPatch = new InternalObfuPatcher();
	private final InternalBytecode intBytecode = new InternalBytecode();
	private final InternalStack intStack =  new InternalStack();
	private Button btnObfuPatch, btnStringPatch, btnBytecode, btnStack;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnObfuPatch = new Button("Obfuscator Patcher");
		btnStringPatch = new Button("StringOb Patcher");
		btnBytecode = new Button("Edit Bytecode");
		btnStack = new Button("Interactive Stack");
		btnObfuPatch.setDisable(true);
		btnStringPatch.setDisable(true);
		btnBytecode.setDisable(true);
		btnStack.setDisable(true);
		btnObfuPatch.setOnAction(new ShowStage(this, intObfuPatch));
		btnStringPatch.setOnAction(new ShowStage(this, intStringPatch));
		btnBytecode.setOnAction(new ShowStage(this, intBytecode));
		btnStack.setOnAction(new ShowStage(this, intStack));
		return new VerticalBar<Button>(1, btnObfuPatch, btnStringPatch, btnBytecode, btnStack);
	}

	@Override
	protected BorderPane createOtherStuff() {
		BorderPane bp = new BorderPane();
		return create(bp);
	}

	@Override
	public void targetLoaded() {
		btnObfuPatch.setDisable(false);
		btnStringPatch.setDisable(false);
		//btnBytecode.setDisable(false);
		btnStack.setDisable(false);
	}
	
	public void show(InternalTab tab){
		if (!tab.isSetup()){
			tab.setup();
		}
		otherControls = tab;
		update();
	}
}
