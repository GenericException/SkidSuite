package me.lpk.gui.tabs.internal;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import me.lpk.asm.deob.EnumDeobfuscation;
import me.lpk.gui.controls.HorizontalBar;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.event.patch.PatchObfuscator;

public class InternalStringPatch extends InternalTab {
	private static String obClass, obMethod;
	private TextField txtClass, txtMethodName;
	private Button btnDecrypt;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnDecrypt = new Button("Decrypt");
		btnDecrypt.setOnAction(new PatchObfuscator(EnumDeobfuscation.SimpleStrings));
		return new VerticalBar<Button>(1, btnDecrypt);
	}

	@Override
	protected BorderPane createOtherStuff() {
		txtClass = new TextField("example/ObfuClass");
		txtClass.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldStr, String newStr) {
				obClass = newStr;
			}
		});
		txtMethodName = new TextField("obfuMethodName");
		txtMethodName.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldStr, String newStr) {
				obMethod = newStr;
			}
		});
		HorizontalBar<TextField> h = new HorizontalBar<TextField>(1, txtClass, txtMethodName);
		BorderPane bpObfuInfo = new BorderPane();
		bpObfuInfo.setCenter(h);
		return bpObfuInfo;

	}

	public static String getObClass() {
		return obClass;
	}

	public static String getObMethod() {
		return obMethod;
	}

	@Override
	public void targetLoaded() {
	}
}
