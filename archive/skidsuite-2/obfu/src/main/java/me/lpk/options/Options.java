package me.lpk.options;

import java.util.HashMap;
import java.util.Map;

import me.lpk.gui.SkidGUI;
import me.lpk.gui.panel.SettingsBox;
import me.lpk.gui.panel.SettingsPanel;
import me.lpk.lang.Lang;

public class Options {
	public static Map<String, Boolean> defaultEnabledStates = new HashMap<String, Boolean>();
	
	static {
		defaultEnabledStates.put(Lang.OPTION_OPTIM_CLASS_REMOVE_SRC, true);
		defaultEnabledStates.put(Lang.OPTION_OPTIM_METHOD_REMOVE_LINES, true);
		defaultEnabledStates.put(Lang.OPTION_OPTIM_METHOD_REMOVE_LOCALNAME, true);
		defaultEnabledStates.put(Lang.OPTION_OPTIM_METHOD_REMOVE_PARAMNAME, true);
	}
	
	public static boolean getDefaultState(String setting){
		return defaultEnabledStates.containsKey(setting) && defaultEnabledStates.get(setting).booleanValue();
	}
	
	public static Map<String, Boolean> boolsFromGui(SkidGUI gui) {
		Map<String, Boolean> values = new HashMap<String, Boolean>();
		for (SettingsPanel panel : gui.getSettingPanels()) {
			for (String group : panel.getGroupNames()) {
				SettingsBox settingBox = panel.getGroup(group);
				for (String setting : settingBox.getBoolSettings()) {
					values.put(setting, settingBox.getSettingValueBool(setting));
				}
			}
		}
		return values;
	}
	public static Map<String, String> stringsFromGui(SkidGUI gui) {
		Map<String, String> values = new HashMap<String, String>();
		for (SettingsPanel panel : gui.getSettingPanels()) {
			for (String group : panel.getGroupNames()) {
				SettingsBox settingBox = panel.getGroup(group);
				for (String setting : settingBox.getStringSettings()) {
					values.put(setting, settingBox.getSettingValueString(setting));
				}
			}
		}
		return values;
	}
}
