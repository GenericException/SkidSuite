package me.lpk.gui.tabs.internal;

import me.lpk.gui.tabs.BasicTab;

public abstract class InternalTab extends BasicTab {
	private boolean setup = false;

	public void setup() {
		super.setup();
		setup = true;
	}

	public boolean isSetup() {
		return setup;
	}
}
