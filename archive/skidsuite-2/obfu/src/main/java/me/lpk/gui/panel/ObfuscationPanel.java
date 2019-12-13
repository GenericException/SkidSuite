package me.lpk.gui.panel;

import me.lpk.lang.Lang;

public class ObfuscationPanel extends SettingsPanel {
	private static final long serialVersionUID = 12222L;

	@Override
	public void setup() {
		SettingsBox renaming = createGroup(Lang.translations.get(Lang.GUI_OBFUSCATION_GROUP_RENAME));
			renaming.addSetting(Lang.OPTION_OBFU_RENAME_ENABLED, false);
			renaming.addSetting(Lang.OPTION_OBFU_RENAME_PRIVATE_ONLY, false);
			renaming.addSetting(Lang.OPTION_OBFU_RENAME_ALPHABET_CLASS, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_");
			renaming.addSetting(Lang.OPTION_OBFU_RENAME_ALPHABET_FIELD, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_");
			renaming.addSetting(Lang.OPTION_OBFU_RENAME_ALPHABET_METHOD, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_");
			renaming.addSetting(Lang.OPTION_OBFU_RENAME_ALPHABET_LOCALS, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_");
		addGroup(renaming);
		addGroup(createBoolGroup(
				Lang.translations.get(Lang.GUI_OBFUSCATION_GROUP_STRING), 
					Lang.OPTION_OBFU_STRINGS_INTOARRAY));
		addGroup(createBoolGroup(
				Lang.translations.get(Lang.GUI_OBFUSCATION_GROUP_FLOW), 
					Lang.OPTION_OBFU_FLOW_TRYCATCH,
					Lang.OPTION_OBFU_FLOW_GOTOFLOOD,
					Lang.OPTION_OBFU_FLOW_MERGE_FIELDS,
					Lang.OPTION_OBFU_FLOW_MATH));
		addGroup(createBoolGroup(
				Lang.translations.get(Lang.GUI_OBFUSCATION_GROUP_ANTI), 
				Lang.OPTION_OBFU_ANTI_VULN_POP2,
				Lang.OPTION_OBFU_ANTI_VULN_EXCRET,
				Lang.OPTION_OBFU_ANTI_VULN_VARS,
				Lang.OPTION_OBFU_ANTI_VULN_LDC,
				Lang.OPTION_OBFU_ANTI_OBJECT_LOCALS,
				Lang.OPTION_OBFU_ANTI_SYNTHETIC));
		}

}
