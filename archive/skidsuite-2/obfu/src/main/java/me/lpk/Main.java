package me.lpk;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import me.lpk.log.Logger;
import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappingProcessor;
import me.lpk.obfuscation.MiscAnti;
import me.lpk.obfuscation.Flow;
import me.lpk.obfuscation.Stringer;
import me.lpk.util.AccessHelper;
import me.lpk.util.JarUtils;
import me.lpk.util.Setup;

public class Main {

	public static void main(String[] args) throws Exception {
		obfuscating("In.jar", "Out.jar");
	}

	public static void obfuscating(String jarIn, String jarOut) throws Exception {
		//Classpather.addFile(jarIn);
		Setup.setBypassSetup();
		//LazySetupMaker.setup();
		Setup dat = Setup.get(jarIn, false);
		Map<String, ClassNode> nodes = new HashMap<String, ClassNode>(dat.getNodes());
		Map<String, MappedClass> mappings = new HashMap<String, MappedClass>(dat.getMappings());
		//
		boolean tryCatch = false;
		boolean ldc = false;
		boolean math = false;
		boolean varDupes = false;
		boolean access = false;
		boolean string = false;
		boolean gotos = false;
		boolean badPop = false;
		boolean retObjErr = false;
		boolean mergeFields = true;
		if (mergeFields){
			for (ClassNode cn : nodes.values()) {
				Flow.mergeFields(cn);
			}
		}
		if (tryCatch) {
			Logger.logLow("Modifying - Try Catch");
			for (ClassNode cn : nodes.values()) {
				for (MethodNode mn : cn.methods) {
					Flow.addTryCatch(mn, "java/lang/Exception", null);
				}
			}
		}
		if (retObjErr) {
			Logger.logLow("Modifying - Bad Return");
			for (ClassNode cn : nodes.values()) {
				MiscAnti.retObjErr(cn);
			}
		}
		if (badPop) {
			Logger.logLow("Modifying - Bad Pop");
			for (ClassNode cn : nodes.values()) {
				for (MethodNode mn : cn.methods) {
					MiscAnti.badPop(mn);
				}
			}
		}
		if (access) {
			Logger.logLow("Modifying - Member Access");
			for (ClassNode cn : nodes.values()) {
				for (FieldNode fn : cn.fields) {
					if (!AccessHelper.isSynthetic(fn.access)) {
						fn.access = fn.access | Opcodes.ACC_SYNTHETIC;
					}
				}
				for (MethodNode mn : cn.methods) {
					if (mn.name.contains("<")) {
						continue;
					}
					if (!AccessHelper.isSynthetic(mn.access)) {
						mn.access = mn.access | Opcodes.ACC_SYNTHETIC;
					}
					if (!AccessHelper.isBridge(mn.access)) {
						mn.access = mn.access | Opcodes.ACC_BRIDGE;
					}
				}
			}
		}
		if (string) {
			Logger.logLow("Modifying - Encryption");
			for (ClassNode cn : nodes.values()) {
				Stringer.stringEncrypt(cn);
			}
		}
		if (gotos) {
			Logger.logLow("Modifying - Flow Obfuscation");
			for (ClassNode cn : nodes.values()) {
				for (MethodNode mn : cn.methods) {
					for (int i = 0; i < 10; i++) {
						Flow.randomGotos( mn);
					}
				}
			}
		}
		if (varDupes) {
			Logger.logLow("Modifying - Var Dupes");
			for (ClassNode cn : nodes.values()) {
				for (MethodNode mn : cn.methods) {
					MiscAnti.duplicateVars(mn);
				}
			}
		}
		if (ldc) {
			Logger.logLow("Modifying - Massive LDC");
			for (ClassNode cn : nodes.values()) {
				for (MethodNode mn : cn.methods) {
					MiscAnti.massiveLdc(mn);
				}
			}
		}
		if (math) {
			Logger.logLow("Modifying - Math obfuscation");
			for (ClassNode cn : nodes.values()) {
				for (MethodNode mn : cn.methods) {
					MiscAnti.breakMath(mn);
				}
			}
		}
		//
		//
		saveJar(jarOut, new File(jarIn), nodes, mappings);
		Logger.logLow("Finished!");
	}

	private static void saveJar(String name, File nonEntriesJar, Map<String, ClassNode> nodes, Map<String, MappedClass> mappedClasses) {
		Map<String, byte[]> out = null;
		out = MappingProcessor.process(nodes, mappedClasses, false);
		try {
			out.putAll(JarUtils.loadNonClassEntries(nonEntriesJar));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int renamed = 0;
		for (MappedClass mc : mappedClasses.values()) {
			if (mc.isTruelyRenamed()) {
				renamed++;
			}
		}
		Logger.logLow("Saving...  [Ranemed " + renamed + " classes]");
		JarUtils.saveAsJar(out, name);
	}

}
