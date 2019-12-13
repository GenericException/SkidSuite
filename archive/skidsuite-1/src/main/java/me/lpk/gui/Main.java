package me.lpk.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.lpk.gui.controls.TabContainer;
import me.lpk.util.Classpather;

public class Main extends Application {
	private static final double VERSION = 2.0;
	private static File jarComparison, jarTarget;
	private static Set<TabContainer> tabs = new HashSet<TabContainer>();

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BorderPane root = new BorderPane();
		TabPane tabPane = new TabPane();
		// Iterate and create TabContainers for each tab type
		for (EnumTab tabType : EnumTab.values()) {
			Tab tab = new Tab();
			tab.setText(tabType.getName());
			tab.setClosable(false);
			TabContainer tc = tabType.createTab();
			tabs.add(tc);
			tab.setContent(tc);
			tabPane.getTabs().add(tab);
		}
		root.setCenter(tabPane);
		//
		stage.setTitle("SkidGUI " + VERSION);
		stage.setScene(new Scene(root, 900, 450));
		stage.show();
	}

	public static File getComparisonJar() {
		return jarComparison;
	}

	public static File getTargetJar() {
		return jarTarget;
	}

	public static void setComparisonJar(File jarBase) {
		// Nothing needs to be notified at the moment that the comparison jar
		// has been loaded. Later it will need to be implemented.
		Main.jarComparison = jarBase;
		loadJar(jarBase);
	}

	public static void setTargetJar(File jarTarget) {
		Main.jarTarget = jarTarget;
		loadJar(jarTarget);
		// Notify the other tabs that the main jar has been loaded.
		for (TabContainer tabContainer : tabs) {
			if (tabContainer == null) {
				continue;
			}
			tabContainer.getTab().targetLoaded();
		}
	}

	private static void loadJar(File jarFile) {
		try {
			Classpather.addFile(jarFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
