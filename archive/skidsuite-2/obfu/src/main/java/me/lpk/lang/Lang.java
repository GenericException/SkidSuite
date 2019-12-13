package me.lpk.lang;

import java.util.HashMap;
import java.util.Map;

public class Lang {
	public static final Map<String, String> translations = new HashMap<String, String>();
	public static final String 
		OPTION_OPTIM_CLASS_REMOVE_SRC =        "optimize.class.remove.src",
		OPTION_OPTIM_CLASS_REMOVE_ANNO =       "optimize.class.remove.annotations",
		OPTION_OPTIM_CLASS_REMOVE_ATRIB =      "optimize.class.remove.attributes",
		OPTION_OPTIM_CLASS_REMOVE_MEMBERS =    "optimize.class.remove.methods",
		OPTION_OPTIM_METHOD_REMOVE_PARAMNAME = "optimize.method.remove.param",
		OPTION_OPTIM_METHOD_REMOVE_ANNO =      "optimize.method.remove.annotations",
		OPTION_OPTIM_METHOD_REMOVE_LOCALNAME = "optimize.method.remove.localdata",
		OPTION_OPTIM_METHOD_REMOVE_LINES =     "optimize.method.remove.lines",
		OPTION_OPTIM_METHOD_REMOVE_ATTRIB =    "optimize.method.remove.attributes",
		OPTION_OPTIM_METHOD_REMOVE_FRAMES =    "optimize.method.remove.frames",
		OPTION_OBFU_STRINGS_INTOARRAY =        "obfuscate.strings.intoarray",
		OPTION_OBFU_ANTI_SYNTHETIC =           "obfuscate.anti.synthetic",
		//OPTION_OBFU_ANTI_DECOMPILE_VULNS =     "obfuscate.anti.decompilevulns",
		OPTION_OBFU_ANTI_VULN_POP2 = "obfuscate.anti.vuln.pop2",
		OPTION_OBFU_ANTI_VULN_VARS = "obfuscate.anti.vuln.vars",
		OPTION_OBFU_ANTI_VULN_LDC = "obfuscate.anti.vuln.ldc",
		OPTION_OBFU_ANTI_VULN_EXCRET = "obfuscate.anti.vuln.excret",
		OPTION_OBFU_ANTI_OBJECT_LOCALS =       "obfuscate.anti.localobject",
		OPTION_OBFU_FLOW_TRYCATCH =            "obfuscate.flow.synthetic",
		OPTION_OBFU_FLOW_MERGE_FIELDS =            "obfuscate.flow.merge.fields",
		OPTION_OBFU_FLOW_GOTOFLOOD =           "obfuscate.flow.gotoflood",
		OPTION_OBFU_FLOW_MATH =                "obfuscate.flow.math",
		OPTION_OBFU_RENAME_ALPHABET_CLASS =    "obfuscate.rename.alphabet.class",
		OPTION_OBFU_RENAME_ALPHABET_METHOD =   "obfuscate.rename.alphabet.method",  
		OPTION_OBFU_RENAME_ALPHABET_FIELD =    "obfuscate.rename.alphabet.field",  
		OPTION_OBFU_RENAME_ALPHABET_LOCALS =    "obfuscate.rename.alphabet.locals",  
		OPTION_OBFU_RENAME_ENABLED  =          "obfuscate.rename.enabled",
		OPTION_OBFU_RENAME_PRIVATE_ONLY  =          "obfuscate.rename.privonly",
		//
		GUI_OBFUSCATION_GROUP_STRING = "gui.groups.obfuscate.strings",
		GUI_OBFUSCATION_GROUP_FLOW =   "gui.groups.obfuscate.flow",
		GUI_OBFUSCATION_GROUP_ANTI =   "gui.groups.obfuscate.anti",
		GUI_OBFUSCATION_GROUP_RENAME =   "gui.groups.obfuscate.rename",
		GUI_OPTIM_GROUP_CLASS =        "gui.groups.optimize.class",
		GUI_OPTIM_GROUP_METHOD =       "gui.groups.optimize.method"
		;
	
	static {
		/* TODO: Loading from language files */
		translations.put(OPTION_OPTIM_CLASS_REMOVE_SRC,   "Remove sourcename");
		translations.put(OPTION_OPTIM_CLASS_REMOVE_ANNO,  "Remove annotations");
		translations.put(OPTION_OPTIM_CLASS_REMOVE_ATRIB, "Remove attributes");
		translations.put(OPTION_OPTIM_METHOD_REMOVE_PARAMNAME, "Remove parameter names");
		translations.put(OPTION_OPTIM_METHOD_REMOVE_ANNO,      "Remove annotations");
		translations.put(OPTION_OPTIM_CLASS_REMOVE_MEMBERS,    "Remove unused members");
		translations.put(OPTION_OPTIM_METHOD_REMOVE_LOCALNAME, "Remove variable names");
		translations.put(OPTION_OPTIM_METHOD_REMOVE_LINES,     "Remove line numbers");
		translations.put(OPTION_OPTIM_METHOD_REMOVE_ATTRIB,    "Remove attributes");
		translations.put(OPTION_OPTIM_METHOD_REMOVE_FRAMES,    "Remove frames");
		//
		translations.put(OPTION_OBFU_STRINGS_INTOARRAY,  "Break into arrays");
		translations.put(OPTION_OBFU_FLOW_TRYCATCH,      "Add try catches");
		translations.put(OPTION_OBFU_FLOW_GOTOFLOOD,     "Add redundant gotos");
		translations.put(OPTION_OBFU_FLOW_MATH,          "Add redundant math");
		translations.put(OPTION_OBFU_FLOW_MERGE_FIELDS,  "Merge static fields");
		translations.put(OPTION_OBFU_ANTI_SYNTHETIC,     "Mark as synthetic");
		translations.put(OPTION_OBFU_ANTI_OBJECT_LOCALS,  	"Remove variable types");
		translations.put(OPTION_OBFU_ANTI_VULN_POP2 ,		"Bad Pop");
		translations.put(OPTION_OBFU_ANTI_VULN_VARS, 		"Duplicate Vars");
		translations.put(OPTION_OBFU_ANTI_VULN_EXCRET ,		"Excepted Return");		
		translations.put(OPTION_OBFU_ANTI_VULN_LDC ,		"Max-Size LDC");		
		translations.put(OPTION_OBFU_RENAME_ALPHABET_CLASS,     "Class Renaming alphabet");
		translations.put(OPTION_OBFU_RENAME_ALPHABET_METHOD,    "Method Renaming alphabet");
		translations.put(OPTION_OBFU_RENAME_ALPHABET_FIELD,     "Field Renaming alphabet");
		translations.put(OPTION_OBFU_RENAME_ALPHABET_LOCALS,    "Local Renaming alphabet");
		translations.put(OPTION_OBFU_RENAME_ENABLED,     		"Rename classes & members");
		translations.put(OPTION_OBFU_RENAME_PRIVATE_ONLY,       "Only rename private members");
		//
		translations.put(GUI_OBFUSCATION_GROUP_STRING,   "String Settings");
		translations.put(GUI_OBFUSCATION_GROUP_FLOW,     "Flow Settings");
		translations.put(GUI_OBFUSCATION_GROUP_ANTI,     "Misc Settings");
		translations.put(GUI_OBFUSCATION_GROUP_RENAME,   "Renaming");
		//
		translations.put(GUI_OPTIM_GROUP_CLASS,     "Class Settings");
		translations.put(GUI_OPTIM_GROUP_METHOD,     "Method Settings");
	}
}
