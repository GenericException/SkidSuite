package me.lpk.asm.threat;

public enum EnumThreatType {
	/* Done: True  */ Class_NonNormal_Synthetic(35,	"Class.Synth", 			"An unnatural ratio of synthetic to non-synthetic objects detected in class structures."),
	/* Done: True  */ Class_RegEdit(90,				"Class.RegEditor", 		"The class structure resembles a java registry editor."),
	/* Done: True  */ Class_Classloader(5,			"Java.ClassLoader", 	"The class extends ClassLoader."),
	//-------------------------------------------------------------------------------------------------//
	/* Done: False */ Method_Call_Library_JNI(10,	"LibCall.JNI", 			"Native calls using the JNI library."),
	/* Done: True  */ Method_Call_Library_JNA(10, 	"LibCall.JNI",			"Native calls using the JNA library."),
	/* Done: True  */ Method_Call_Preferences_Root(60,"MthdRef.Preferences", "Access to user preferences on their machine."),
	/* Done: True  */ Method_Call_Classloader(10,	"MthdRef.ClassLoader", 	"Loading classes into the JVM"),
	/* Done: True  */ Method_Call_ClassloaderResource(5,"MthdRef.ResourceLoader", "Loading resources into the JVM."),
	/* Done: True  */ Method_Call_RuntimeExec(30, 	"MthdRef.RuntimeExec",	"Calls to Runtime.exec allowing external programs to be run."),
	/* Done: True  */ Method_Call_RuntimeLoad(30, 	"MthdRef.RuntimeLoad",	"Calls to Runtime.load allowing external use of native libraries."),
	/* Done: True  */ Method_Call_File_Delete(2, 	"MthdRef.FileDel",		"Files deleted from the disc."),
	/* Done: True  */ Method_Call_File_Write(2, 	"MthdRef.FileWrite",	"Files written to the disc."),
	/* Done: False */ Method_Call_File_Read(1, 		"MthdRef.FileRead",		"Files read from the disc."),
	//-------------------------------------------------------------------------------------------------//
	/* Done: True  */ Method_Call_TextToBytes(10, 	"MthdRef.TxtToBytes",	"Text to bytes. May be done so for serialization purposes."),
	/* Done: True  */ Method_Call_BytesToText(20, 	"MthdRef.BytesToTxt",	"Bytes to text. May be done to hide strings from decompilers."),
	/* Done: False */ Method_Obfuscation_DashO_Strings(40,"Obfu.DashO.Strings","DashO obfuscator detected."),
	/* Done: False */ Method_Obfuscation_Allatori(40,"Obfu.Allatori",		"Allatori obfuscator detected."),
	/* Done: False */ Method_Obfuscation_Stringer(40,"Obfu.Stringer",		"Stringer obfuscator detected."),
	/* Done: False */ Method_Obfuscation_ZKM(40, 	"Obfu.ZKM",				"ZKM obfuscator detected."),
	/* Done: False */ Method_Obfuscation_JCrypt(40, "Obfu.JCrypt",			"JCrypt obfuscator detected."),
	/* Done: True  */ Method_Obfuscation_StringInStringOut(1, "Obfu.Basic.Strings","String to string method call. May be used for string encryption."),
	//-------------------------------------------------------------------------------------------------//
	/* Done: True  */ String_FileURL(40, 			"String.FileURL",		"Direct link to online file."),
	/* Done: True  */ String_WebURL(10, 			"String.WebURL",		"Link to online site detected."),
	/* Done: True  */ String_Registry(5, 			"String.WinReg",		"Windows registry key detected."),
	/* Done: True  */ String_Unicode(1, 			"String.Unicode",		"Unicode characters detected.");
	private final String name, desc;
	private final int points;
	EnumThreatType(int warn,String name, String desc){
		this.points = warn;
		this.name = name;
		this.desc = desc;
	}
	public int getPoints(){
		return points;
	}
	public String getName(){
		return name;
	}
	public String getDesc(){
		return desc;
	}
}
