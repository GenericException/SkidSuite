package me.lpk.gui;

import me.lpk.gui.controls.TabContainer;
import me.lpk.gui.tabs.AnalyzeTab;
import me.lpk.gui.tabs.BasicTab;
import me.lpk.gui.tabs.HomeTab;

import me.lpk.gui.tabs.ObfuscationTab;
import me.lpk.gui.tabs.PatchingTab;

public enum EnumTab {
	TabHome("Home"), TabPatch("Patching"), TabObfuscation("Obfuscation"), TabAnalyze("Analyze");

	private final String name;

	EnumTab(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public TabContainer createTab() {
		TabContainer tab = new TabContainer(genScene());
		tab.setCenter(tab.getTab());
		return tab;
	}

	private BasicTab genScene() {
		BasicTab bs = null;
		switch (this) {
		case TabHome:
			bs = new HomeTab();
			break;
		case TabObfuscation:
			bs = new ObfuscationTab();
			break;
		case TabAnalyze:
			bs = new AnalyzeTab();
			break;
		case TabPatch:
			bs = new PatchingTab();
			break;
		default:
			break;
		}
		bs.setup();
		return bs;
	}
}
