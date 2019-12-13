package me.lpk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import me.lpk.gui.ErrorDialog;
import me.lpk.lang.Lang;
import me.lpk.log.Logger;
import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappingProcessor;
import me.lpk.mapping.MappingRenamer;
import me.lpk.mapping.loaders.EnigmaLoader;
import me.lpk.obfuscation.Flow;
import me.lpk.obfuscation.MiscAnti;
import me.lpk.obfuscation.Stringer;
import me.lpk.obfuscation.rename.MappingModeImpl;
import me.lpk.obfuscation.rename.ModeSkidfuscate;
import me.lpk.optimization.Optimizer;
import me.lpk.util.JarUtils;
import me.lpk.util.Setup;

public class Skidfuscate {
	private static Skidfuscate instance;
	private static Map<String, File> libraries = new HashMap<String, File>();

	public static Skidfuscate get() {
		if (instance == null) {
			instance = new Skidfuscate();
		}
		return instance;
	}

	public void addLibrary(File jar) {
		libraries.put(jar.getAbsolutePath(), jar);
	}

	public void parse(File jar, Map<String, Boolean> boolOpts, Map<String, String> strOpts) {
		// Order: Load --> Optimize --> Obfuscation --> Renaming
		try {
			Logger.logLow("Loading & mapping from jar...");
			Setup dat = Setup.get(jar.getAbsolutePath(), true, new ArrayList<File>(libraries.values()));
			Map<String, ClassNode> nodes = new HashMap<String, ClassNode>(dat.getNodes());
			Map<String, MappedClass> mappings = new HashMap<String, MappedClass>(dat.getMappings());

			Optimizer optimizer = new Optimizer(boolOpts);
			optimizer.optimize(jar, nodes, mappings);

			if (boolOpts.get(Lang.OPTION_OBFU_ANTI_OBJECT_LOCALS).booleanValue()) {
				MiscAnti.removeLocalTypes(nodes.values());
			}
			if (boolOpts.get(Lang.OPTION_OBFU_ANTI_SYNTHETIC).booleanValue()) {
				MiscAnti.makeSynthetic(nodes.values());
			}
			if (boolOpts.get(Lang.OPTION_OBFU_ANTI_VULN_EXCRET).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					MiscAnti.retObjErr(cn);
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_ANTI_VULN_POP2).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					for (MethodNode mn : cn.methods) {
						MiscAnti.badPop(mn);
					}
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_ANTI_VULN_LDC).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					for (MethodNode mn : cn.methods) {
						MiscAnti.massiveLdc(mn);
					}
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_ANTI_VULN_VARS).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					for (MethodNode mn : cn.methods) {
						MiscAnti.duplicateVars(mn);
					}
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_FLOW_GOTOFLOOD).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					for (MethodNode mn : cn.methods) {
						Flow.randomGotos(mn);
					}
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_FLOW_MERGE_FIELDS).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					Flow.mergeFields(cn);
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_STRINGS_INTOARRAY).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					Stringer.stringEncrypt(cn);
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_FLOW_TRYCATCH).booleanValue()) {
				for (ClassNode cn : nodes.values()) {
					for (MethodNode mn : cn.methods) {
						Flow.addTryCatch(mn, "java/lang/Exception", null);
					}
				}
			}
			if (boolOpts.get(Lang.OPTION_OBFU_RENAME_ENABLED).booleanValue()) {
				Logger.logLow("Remapping classes...");
				doRemapping(mappings, strOpts, boolOpts, nodes.values());
			}

			Logger.logLow("Saving mappings...");
			new EnigmaLoader().save(mappings, new File(jar.getName().replace(".jar", ".enigma")));
			Logger.logLow("Destroying stack frames...");
			destroyStackFrames(nodes.values());
			Logger.logLow("Saving jar...");
			saveJar(jar.getName().replace(".jar", "-re.jar"), jar, nodes, mappings);
			Logger.logLow("Done!");
		} catch (Exception e) {
			ErrorDialog.show(e);
		}
	}

	private void destroyStackFrames(Collection<ClassNode> values) {
		for (ClassNode cn : values) {
			for (MethodNode mn : cn.methods) {
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain.getType() == AbstractInsnNode.FRAME) {
						mn.instructions.remove(ain);
					}
				}
			}
		}
	}

	/**
	 * Remapps classes, fields, & methods. Then renames local variables.
	 * 
	 * @param mappings
	 * @param strOpts
	 * @param nodes
	 */
	private void doRemapping(Map<String, MappedClass> mappings, Map<String, String> strOpts, Map<String, Boolean> boolOpts, Collection<ClassNode> nodes) {
		// TODO: Other modes for different situations.
		// ModeSkidfuscate
		// ModeSimple
		MappingModeImpl mode = new ModeSkidfuscate(strOpts.get(Lang.OPTION_OBFU_RENAME_ALPHABET_CLASS), strOpts.get(Lang.OPTION_OBFU_RENAME_ALPHABET_FIELD),
				strOpts.get(Lang.OPTION_OBFU_RENAME_ALPHABET_METHOD), boolOpts.get(Lang.OPTION_OBFU_RENAME_PRIVATE_ONLY).booleanValue());
		new MappingRenamer().remapClasses(mappings, mode);
		for (ClassNode cn : nodes) {
			cn.innerClasses.clear();
			cn.outerClass = null;
			for (MethodNode mn : cn.methods) {
				int i = 0;
				if (mn.parameters != null) {
					for (ParameterNode pn : mn.parameters) {
						pn.name = mode.getName(strOpts.get(Lang.OPTION_OBFU_RENAME_ALPHABET_LOCALS), i++);
					}
				}
				if (mn.localVariables != null) {
					for (LocalVariableNode lvn : mn.localVariables) {
						lvn.name = mode.getName(strOpts.get(Lang.OPTION_OBFU_RENAME_ALPHABET_LOCALS), i++);
					}
				}
			}
		}
	}

	/**
	 * Saves given classnodes to a file with a given name, takes resources from
	 * another given jar.
	 * 
	 * @param name
	 * @param nonEntriesJar
	 * @param nodes
	 * @param mappedClasses
	 */
	private static void saveJar(String name, File nonEntriesJar, Map<String, ClassNode> nodes, Map<String, MappedClass> mappedClasses) {
		Map<String, byte[]> out = null;
		out = MappingProcessor.process(nodes, mappedClasses, false);
		try {
			out.putAll(JarUtils.loadNonClassEntries(nonEntriesJar));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JarUtils.saveAsJar(out, name);
	}
}
