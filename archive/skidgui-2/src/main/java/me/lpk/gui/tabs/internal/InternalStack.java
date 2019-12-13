package me.lpk.gui.tabs.internal;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import me.lpk.asm.deob.EnumDeobfuscation;
import me.lpk.gui.controls.HorizontalBar;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.event.patch.PatchObfuscator;
import me.lpk.gui.tabs.internal.InternalBytecode.ChooseMethod;
import me.lpk.gui.tabs.internal.InternalBytecode.InternalChooseButton;
import me.lpk.gui.tabs.internal.InternalBytecode.InternalChooseTree;
import me.lpk.gui.tabs.internal.InternalBytecode.StageSelection;
import me.lpk.gui.tabs.treeview.TreeViewTab;

public class InternalStack extends InternalTab {
	private static String startClass, startMethod;
	private static ClassNode ccn;
	private static MethodNode cmn;
	private Button btnChooseStart, btnSave;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnChooseStart = new Button("Choose Start");
		btnChooseStart.setOnAction(new PatchObfuscator(EnumDeobfuscation.SimpleStrings));
		return new VerticalBar<Button>(1, btnChooseStart);
	}

	@Override
	protected BorderPane createOtherStuff() {
		BorderPane bpObfuInfo = new BorderPane();

		return bpObfuInfo;
	}

	public void setNode(ClassNode newNode, MethodNode mn) {
		ccn = newNode;
		cmn = mn;
		//btnSave.setDisable(false);
		BorderPane bp = new BorderPane();

		 
		bp.setCenter(new Label(ccn.name + ":" + cmn.name));
		otherControls = bp;
		update();
	}

	public static String getObClass() {
		return startClass;
	}

	public static String getObMethod() {
		return startMethod;
	}

	@Override
	public void targetLoaded() {
	}

	
}
