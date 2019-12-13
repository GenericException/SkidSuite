package me.lpk.optimization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import me.lpk.lang.Lang;
import me.lpk.log.Logger;
import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappedMember;
import me.lpk.util.JarUtils;

/**
 * It's ugly but it's less ugly than before.
 * 
 * Still works too.
 */
public class Optimizer {
	private final Map<String, Boolean> boolOpts;

	public Optimizer(Map<String, Boolean> boolOpts) {
		this.boolOpts = boolOpts;
	}

	/**
	 * Moves most of the optimize method in here rather than putting it in the
	 * parsing method.
	 * 
	 * @param jar
	 * @param nodes
	 * @param mappings
	 */
	public void optimize(File jar, Map<String, ClassNode> nodes, Map<String, MappedClass> mappings) {
		Logger.logLow("Beginning optimization...");
		String mainClass = JarUtils.getManifestMainClass(jar);
		boolean hasMain = mainClass != null;
		boolean optionRemove = boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_MEMBERS, false).booleanValue();
		boolean removeMethods = hasMain && optionRemove;

		if (hasMain){
			Logger.logLow("Found main class: " + mainClass);
			Logger.logLow("Searching for unused classes...");
		}else if (optionRemove){
			Logger.logLow("Member removal was enabled, but could not find an entry point! Skipping removal.");
		}
		// TODO: Make remover that removes un-used methods
		Remover remover = new SimpleRemover();
		// Make a new map that does not contain library nodes.
		Map<String, ClassNode> mapForRemoval = new HashMap<String, ClassNode>();
		for (String name : mappings.keySet()) {
			MappedClass mc = mappings.get(name);
			if (!mc.isLibrary() && nodes.containsKey(name)) {
				mapForRemoval.put(name, nodes.get(name));
			}
		}
		List<String> names = new ArrayList<String>();
		if (removeMethods) {
			remover.getUsedClasses(mainClass, mapForRemoval);
			Set<String> keep = remover.getKeptClasses();
			Logger.logLow("Removing unused classes [" + (keep.size() - mapForRemoval.size()) + " marked]...");
			Set<String> set = new HashSet<String>(nodes.keySet());
			for (String name : set) {
				if (!keep.contains(name)) {
					nodes.remove(name);
					mappings.remove(name);
				}
			}
			names.addAll(keep);
		} else {
			names.addAll(nodes.keySet());
		}
		Logger.logLow("Optimizing classes...");
		for (String name : names) {
			try {
				MappedClass mc = mappings.get(name);
				if (mc == null) {
					continue;
				}
				// TODO: Was using ASM's optimizer. Now I'm not.
				// So I have to remake the FieldVisitor.
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				ClassVisitor remapper = new ClassOptimizerImpl(remover, mc, cw);
				mc.getNode().accept(remapper);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void shrink(MappedClass cn, Remover r, Remapper m, ClassWriter cw) {
		try {
			ClassVisitor remapper = new ClassOptimizerImpl(r, cn, cw);
			cn.getNode().accept(remapper);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, getErr(e), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String getErr(Exception e) {
		String s = "";
		for (StackTraceElement ste : e.getStackTrace()) {
			s += ste.toString() + "\n";
		}
		return s;
	}

	class ClassOptimizerImpl extends ClassVisitor {
		private final Remover rem;
		private final MappedClass mapped;

		public ClassOptimizerImpl(Remover r, MappedClass mappedClass, ClassWriter cw) {
			super(Opcodes.ASM5, cw);
			this.rem = r;
			this.mapped = mappedClass;
		}

		@Override
		public void visitSource(String source, String debug) {
			// remove debug
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_SRC, true) && cv != null) {
				cv.visitSource(source, debug);
			}
		}

		@Override
		public void visitOuterClass(final String owner, final String name, final String desc) {
			// remove debug info
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_ATRIB, true) && cv != null) {
				cv.visitOuterClass(owner, name, desc);
			}
		}

		@Override
		public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
			// remove debug info
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_SRC, true) && cv != null) {
				cv.visitInnerClass(name, outerName, innerName, access);
			}
		}

		@Override
		public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
			// remove annotations
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_ANNO, true) && cv != null) {
				return cv.visitAnnotation(desc, visible);
			}
			return null;
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// remove annotations
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_ANNO, true) && cv != null) {
				return cv.visitTypeAnnotation(typeRef, typePath, desc, visible);
			}
			return null;
		}

		@Override
		public void visitAttribute(final Attribute attr) {
			// remove non standard attributes
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_ATRIB, true) && cv != null) {
				cv.visitAttribute(attr);
			}
		}

		@Override
		public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
			// remove signature
			boolean remove = boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_SRC, true);
			return super.visitField(access, name, desc, remove ? null : signature, value);
		}

		@Override
		public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
			// Cancelling what ClassOptimizer does
			if (rem.isMethodUsed(mapped.getNewName(), name + desc) || isOverride(name, desc)) {
				boolean remove = boolOpts.getOrDefault(Lang.OPTION_OPTIM_CLASS_REMOVE_SRC, true);
				return createMethodVisitor(super.visitMethod(access, name, desc, remove ? null : signature, exceptions));
			}
			return null;
		}

		private boolean isOverride(String name, String desc) {
			for (MappedMember mm : mapped.getMethods()) {
				if (mm.getNewName().equals(name) && mm.getDesc().equals(desc)) {
					return mm.doesOverride();
				}
			}
			return false;
		}
		
		private MethodVisitor createMethodVisitor(MethodVisitor mv) {
			return new MethodOptimizerImpl(mv);
		}
	}

	class MethodOptimizerImpl extends MethodVisitor {
		public MethodOptimizerImpl(MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitParameter(String name, int access) {
			// remove parameter info
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_PARAMNAME, true) && mv != null) {
				mv.visitParameter(name, access);
			}
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			// remove annotations
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_ANNO, true) && mv != null) {
				mv.visitAnnotationDefault();
			}
			return null;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			// remove annotations
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_ANNO, true) && mv != null) {
				mv.visitAnnotation(desc, visible);
			}
			return null;
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// remove annotations
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_ANNO, true) && mv != null) {
				mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
			}
			return null;
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
			// remove annotations
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_ANNO, true) && mv != null) {
				mv.visitParameterAnnotation(parameter, desc, visible);
			}
			return null;
		}

		@Override
		public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
			// remove debug info
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_LOCALNAME, true) && mv != null) {
				mv.visitLocalVariable(name, desc, signature, start, end, index);
			}
		}

		@Override
		public void visitLineNumber(final int line, final Label start) {
			// remove debug info
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_LINES, true) && mv != null) {
				mv.visitLineNumber(line, start);
			}
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			// remove frames
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_FRAMES, true) && mv != null) {
				mv.visitFrame(type, nLocal, local, nStack, stack);
			}
		}

		@Override
		public void visitAttribute(Attribute attr) {
			// remove non standard attributes
			if (!boolOpts.getOrDefault(Lang.OPTION_OPTIM_METHOD_REMOVE_ATTRIB, true) && mv != null) {
				mv.visitAttribute(attr);
			}
		}
	}

}
