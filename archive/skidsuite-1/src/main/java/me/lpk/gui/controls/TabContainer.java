package me.lpk.gui.controls;

import javafx.scene.layout.BorderPane;
import me.lpk.gui.tabs.BasicTab;

/**
 * A BorderPane that keeps track of it's tab
 */
public class TabContainer extends BorderPane {
	private final BasicTab tab;

	public TabContainer(BasicTab tab) {
		this.tab = tab;
	}

	public BasicTab getTab() {
		return tab;
	}
}
