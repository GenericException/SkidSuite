package me.lpk.gui.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import me.lpk.asm.threat.EnumThreatType;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.event.Analyze;

public class AnalyzeTab extends BasicTab {
	private static final Map<EnumThreatType, CheckBox> dank = new HashMap<EnumThreatType, CheckBox>();
	private static TextField txtPackages;
	private Button btnIP, btnURL, btnBytecode;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnIP = new Button("Dump IPs");
		btnURL = new Button("Dump URLs");
		btnBytecode = new Button("Static Analysis");
		btnIP.setDisable(true);
		btnURL.setDisable(true);
		btnBytecode.setDisable(true);
		btnIP.setOnAction(new Analyze(Analyze.DUMP_IP));
		btnURL.setOnAction(new Analyze(Analyze.DUMP_URL));
		btnBytecode.setOnAction(new Analyze(Analyze.SUSP_REP));
		return new VerticalBar<Button>(1, btnIP, btnURL, btnBytecode);
	}

	@Override
	protected BorderPane createOtherStuff() {
		BorderPane bp = new BorderPane();
		VBox v = new VBox(2);
		VBox v2 = new VBox(EnumThreatType.values().length);
		VBox v3 = new VBox(2);
		v3.getChildren().add(new Label("Ignored packages (Separate by comma):"));
		txtPackages = new TextField("com/example,me/another");
		v3.getChildren().add(txtPackages);
		for (EnumThreatType type : EnumThreatType.values()) {
			CheckBox chkThreat = new CheckBox("Ignore: " + type.getName());
			v2.getChildren().add(chkThreat);
			dank.put(type, chkThreat);
		}
		v.getChildren().add(v3);
		ScrollPane sp = new ScrollPane(v2);
		v.getChildren().add(sp);
		bp.setCenter(v);
		return create(bp);
	}

	public static List<String> getIgnored() {
		List<String> list = new ArrayList<String>();
		String txt = txtPackages.getText();
		String[] packages = txt.contains(",") ? txt.split(",") : new String[] { txt };
		for (String pkg : packages) {
			if (pkg.length() > 0) {
				list.add(pkg.trim());
			}
		}
		return list;
	}

	public static boolean skip(EnumThreatType type) {
		return dank.get(type).isSelected();
	}

	@Override
	public void targetLoaded() {
		btnIP.setDisable(false);
		btnURL.setDisable(false);
		btnBytecode.setDisable(false);
	}
}
