package me.lpk.gui.tabs.internal;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import me.lpk.asm.deob.EnumDeobfuscation;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.event.patch.PatchJCrypt;
import me.lpk.gui.event.patch.PatchObfuscator;

public class InternalObfuPatcher extends InternalTab {
	private Button btnDumpJ, btnPatchZKM, btnPatchAllatori, btnPatchStringer, btnPatchDashO;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnDumpJ = new Button("Patch JCrypt");
		btnPatchZKM = new Button("Patch ZKM (5 & 6)");
		btnPatchDashO = new Button("Patch DashO");
		btnPatchStringer = new Button("Patch Stringer");
		btnPatchStringer.setDisable(true);
		btnPatchAllatori = new Button("Patch Allatori");
		btnDumpJ.setOnAction(new PatchJCrypt());
		btnPatchZKM.setOnAction(new PatchObfuscator(EnumDeobfuscation.ZKM));
		btnPatchDashO.setOnAction(new PatchObfuscator(EnumDeobfuscation.DashO));
		btnPatchAllatori.setOnAction(new PatchObfuscator(EnumDeobfuscation.Allatori));
		btnPatchStringer.setOnAction(new PatchObfuscator(EnumDeobfuscation.Stringer));
		//
		return new VerticalBar<Button>(1, btnDumpJ, btnPatchZKM, btnPatchAllatori, btnPatchStringer, btnPatchDashO);
	}

	@Override
	protected BorderPane createOtherStuff() {
		BorderPane bp = new BorderPane();
		return bp;
	}

	@Override
	public void targetLoaded() {

	}
}