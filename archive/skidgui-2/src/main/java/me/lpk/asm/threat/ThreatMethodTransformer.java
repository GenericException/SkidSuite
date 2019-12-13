package me.lpk.asm.threat;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.asm.threat.types.MethodThreat;
import me.lpk.asm.threat.types.TextThreat;
import me.lpk.asm.threat.types.Threat;
import me.lpk.util.StringUtil;

public class ThreatMethodTransformer extends AbstractMethodTransformer {
	private final ThreatClassVisitor cv;
	private final ClassThreat ct;

	public ThreatMethodTransformer(ClassNode node, ClassThreat ct, ThreatClassVisitor cv) {
		super(node);
		this.cv = cv;
		this.ct = ct;
	}

	@Override
	public void transform(MethodNode method) {
		int curLine = -1, opIndex = 0;
		for (AbstractInsnNode ain : method.instructions.toArray()) {
			opIndex++;
			// Keeping track of the current line.
			if (ain.getType() == AbstractInsnNode.LINE) {
				LineNumberNode lin = (LineNumberNode) ain;
				curLine = lin.line;
			}
			if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode min = (MethodInsnNode) ain;
				// Checks based on owner of the method call
				if (min.owner.startsWith("com/sun/jna")) {
					// Sun's windows registry implementation.
					Threat t = new MethodThreat(EnumThreatType.Method_Call_Library_JNA, getNode().name, method.name, min, curLine, opIndex);
					ct.addThreat(EnumThreatType.Method_Call_Library_JNA, t);
				} else if ((min.owner.equals("java/lang/System") || min.owner.equals("java/lang/Runtime")) && min.name.contains("load") && min.desc.equals("(Ljava/lang/String;)V")) {
					// Loading native via the system runtime.
					Threat t = new MethodThreat(EnumThreatType.Method_Call_RuntimeLoad, getNode().name, method.name, min, curLine, opIndex);
					ct.addThreat(EnumThreatType.Method_Call_RuntimeLoad, t);
				} else if (min.owner.equals("java/lang/Runtime") && min.name.contains("exec")) {
					// Running executables via the system runtime
					Threat t = new MethodThreat(EnumThreatType.Method_Call_RuntimeExec, getNode().name, method.name, min, curLine, opIndex);
					ct.addThreat(EnumThreatType.Method_Call_RuntimeExec, t);
				} else if (cv.isClass(min.owner, "java/lang/ClassLoader")) {
					if (min.name.equals("getSystemResources") || min.name.startsWith("getResource")) {
						// Loading resources via classloader.
						Threat t = new MethodThreat(EnumThreatType.Method_Call_ClassloaderResource, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Call_ClassloaderResource, t);
					}
				} else if (cv.isClass(min.owner, "java/io/File")) {
					// File IO operations
					if (min.name.startsWith("delete")) {
						Threat t = new MethodThreat(EnumThreatType.Method_Call_File_Delete, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Call_File_Delete, t);
					} else if (min.name.equals("createNewFile") || min.name.equals("createTempFile") || min.name.equals("renameTo")) {
						Threat t = new MethodThreat(EnumThreatType.Method_Call_File_Write, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Call_File_Write, t);
					}
				} else if (cv.isClass(min.owner, "java/util/prefs/Preferences")) {
					if (min.name.startsWith("syst") || min.name.startsWith("user")) {
						Threat t = new MethodThreat(EnumThreatType.Method_Call_Preferences_Root, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Call_Preferences_Root, t);
					}
				}

				// Checks based on the description
				if (min.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
					// String to string (Example: rot13)
					if (min.getPrevious().getType() == AbstractInsnNode.LDC_INSN) {
						Threat t = new MethodThreat(EnumThreatType.Method_Obfuscation_StringInStringOut, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Obfuscation_StringInStringOut, t);
					}
				} else if (min.desc.equals("([Ljava.lang.Byte;)Ljava/lang/String;")) {
					// Bytes[] to String
					Threat t = new MethodThreat(EnumThreatType.Method_Call_BytesToText, getNode().name, method.name, min, curLine, opIndex);
					ct.addThreat(EnumThreatType.Method_Call_BytesToText, t);
				} else if (min.desc.equals("(Ljava/lang/String;)[Ljava.lang.Byte;")) {
					// String to Bytes[]
					if (min.getPrevious().getType() == AbstractInsnNode.LDC_INSN) {
						Threat t = new MethodThreat(EnumThreatType.Method_Call_TextToBytes, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Call_TextToBytes, t);
					}
				} else if (min.desc.equals("(Ljava/lang/String;I)Ljava/lang/String;")) {
					// String to Bytes[]
					int prevOp = min.getPrevious().getOpcode();
					boolean isPrevOp = (prevOp >= Opcodes.ICONST_0 && prevOp <= Opcodes.ICONST_5) || prevOp == Opcodes.BIPUSH || prevOp == Opcodes.SIPUSH;
					if (isPrevOp && min.getPrevious().getPrevious().getType() == AbstractInsnNode.LDC_INSN) {
						Threat t = new MethodThreat(EnumThreatType.Method_Obfuscation_DashO_Strings, getNode().name, method.name, min, curLine, opIndex);
						ct.addThreat(EnumThreatType.Method_Obfuscation_DashO_Strings, t);
					}
				}
			} else if (ain.getType() == AbstractInsnNode.LDC_INSN) {
				LdcInsnNode ldc = (LdcInsnNode) ain;
				if (ldc.cst instanceof String) {
					String ldtxt = ldc.cst.toString();
					if (ldtxt.contains("\\Microsoft\\Windows NT\\")) {
						// String of windows registry detected. Only focused on
						// startup keys being accessed.
						Threat t = new TextThreat(EnumThreatType.String_Registry, getNode().name, method.name, ldtxt, curLine, opIndex);
						ct.addThreat(EnumThreatType.String_Registry, t);
					} else if (hasUnicode(ldtxt)) {
						// Unicode string. Possibly encrypted text.
						Threat t = new TextThreat(EnumThreatType.String_Unicode, getNode().name, method.name, ldtxt, curLine, opIndex);
						ct.addThreat(EnumThreatType.String_Unicode, t);
					} else {
						// Lastly check for URLS
						if (ldtxt.length() > 5 && checkFileURL(ldtxt)) {

							// Direct file URL. PHP is considered a web URL
							// instead of a direct file.
							String fileRegex = "(http|https|ftp|ftps)\\:\\/\\/[a-z0-9\\-\\.]+\\.[a-z]{2,3}(\\/[^\\.]*)(.)(\\S*)?";
							if (ldtxt.substring(ldtxt.lastIndexOf("/")).contains(".") && !ldtxt.toLowerCase().matches("^.*\\.(php)") && ldtxt.matches(fileRegex)) {
								// TODO: Give more information based on the
								// groups? (Connection type, domain, filename,
								// etc.)
								/*
								 * Pattern p = Pattern.compile(fileRegex);
								 * Matcher m = p.matcher(ldtxt); if
								 * (m.matches()){ for (int group = 0; group <=
								 * m.groupCount(); group++){
								 * System.out.println(group + ":" +
								 * m.group(group)); } }
								 */
								Threat t = new TextThreat(EnumThreatType.String_FileURL, getNode().name, method.name, ldtxt, curLine, opIndex);
								ct.addThreat(EnumThreatType.String_FileURL, t);
							} else if (ldtxt.contains("/")) {
								Threat t = new TextThreat(EnumThreatType.String_WebURL, getNode().name, method.name, ldtxt, curLine, opIndex);
								ct.addThreat(EnumThreatType.String_WebURL, t);
							}
						}
					}
				}
			}
		}
	}

	private boolean checkFileURL(String string) {
		return StringUtil.isLink(string);
	}

	private boolean hasUnicode(String string) {
		for (char c : string.toCharArray()) {
			if (Character.getNumericValue(c) <= 160) {
				continue;
			}
			return true;
		}
		return false;
	}

}