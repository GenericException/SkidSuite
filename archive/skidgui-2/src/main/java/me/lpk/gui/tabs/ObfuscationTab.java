package me.lpk.gui.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.event.Obfuscate;
import me.lpk.mapping.MappingMode;
import me.lpk.mapping.modes.*;

public class ObfuscationTab extends BasicTab {
	private ComboBox<String> cmbObfuscation;
	private TextField txtOutput;
	private CheckBox chkMetaMain;
	private static final String SIMPLE = "Simple", ABC = "Alphabetical", RAND_SHORT = "Random-Short", RAND_LONG = "Random-Long", UNI1 = "Unicode Witchcraft", USE_ENIGMA = "Use Enigma Mappings";
	private Button btnReob;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnReob = new Button("Obfuscate jar");
		btnReob.setDisable(true);
		//
		btnReob.setOnAction(new Obfuscate(this));
		//
		return new VerticalBar<Button>(1, btnReob);
	}

	@Override
	protected BorderPane createOtherStuff() {
		HBox hObfuType = new HBox(2);
		ObservableList<String> options = FXCollections.observableArrayList(SIMPLE, USE_ENIGMA, ABC, RAND_SHORT, RAND_LONG, UNI1);
		cmbObfuscation = new ComboBox<String>(options);
		cmbObfuscation.setValue(SIMPLE);
		chkMetaMain = new CheckBox("Force Manifest Main Class");
		hObfuType.getChildren().add(new Label("Reobfuscation type:"));
		hObfuType.getChildren().add(cmbObfuscation);
		//
		HBox hExport = new HBox(3);
		txtOutput = new TextField("Obfuscated.jar");
		hExport.getChildren().add(new Label("Exported file name:"));
		hExport.getChildren().add(txtOutput);
		hExport.getChildren().add(chkMetaMain);
		//
		VBox v = new VBox(3);
		v.getChildren().add(hObfuType);
		v.getChildren().add(hExport);
		return create(v);
	}

	@Override
	public void targetLoaded() {
		btnReob.setDisable(false);
		// btnEditor.setDisable(false);
	}

	/**
	 * Returns the mapping mode based on the user's selected option.
	 * 
	 * TODO: Load modes from external jars and have this be more dynamic.
	 */
	public MappingMode getObfuscation() {
		String type = cmbObfuscation.getValue().toString();
		switch (type) {
		case ABC:
			return new ModeAlphabetical();
		case RAND_SHORT:
			return new ModeRandom(1);
		case RAND_LONG:
			return new ModeRandom(475);
		case SIMPLE:
			return new ModeSimple();
		case UNI1:
			return new ModeUnicodeEvil();
		}
		return new ModeNone();
	}

	public boolean forceMeta() {
		return chkMetaMain.isSelected();
	}

	public String getExportedName() {
		return txtOutput.getText();
	}

	public boolean useEnigma() {
		return cmbObfuscation.getValue().toString().equals(USE_ENIGMA);
	}
}
