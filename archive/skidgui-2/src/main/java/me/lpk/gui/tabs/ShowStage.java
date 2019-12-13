package me.lpk.gui.tabs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import me.lpk.gui.tabs.internal.InternalTab;

public class ShowStage implements EventHandler<ActionEvent> {
	private final InternalTab tab;
	private final PatchingTab ptab;

	public ShowStage(PatchingTab ptab, InternalTab tab) {
		this.ptab = ptab;
		this.tab = tab;
	}

	@Override
	public void handle(ActionEvent event) {
		ptab.show(tab);
	}
}
