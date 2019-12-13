package me.lpk.hijack.modder;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import me.lpk.hijack.RemappedName;
import me.lpk.hijack.match.AbstractMatcher;
import me.lpk.hijack.modder.ClassModder;
import me.lpk.util.ASMUtils;

/**
 * A ClassModder that replaces source names with names dictated by a
 * "RemappedName" annotation. Allows for names in the source to be hot-swapped
 * for different names at runtime.
 */
public class ShellReplacementModder extends ClassModder {
	private static final String ANNO_DESC = "L" + RemappedName.class.getName().replace(".", "/") + ";";
	private final String shellPkg;

	public ShellReplacementModder(AbstractMatcher<?> matcher, String shellPkg) {
		super(matcher);
		this.shellPkg = shellPkg;
	}

	@Override
	public void modify(ClassNode cn) {
		// TODO: Replacing interfaces
		// I haven't needed to do this yet, but when I do, it'll be somewhere at
		// the bottom of this method.
		//
		// Iterate methods first. The ClassNode name property and fields are
		// used here so it's important methods get priority.
		for (MethodNode mn : cn.methods) {
			// Method name & desc can be updated right away
			mn.name = updateMethodName(cn, mn);
			mn.desc = updateMethodDesc(mn.desc);
			// Iterating opcodes
			for (AbstractInsnNode ain : mn.instructions.toArray()) {
				if (ain.getType() == AbstractInsnNode.FIELD_INSN) {
					// Name relies on the desc and owner to be renamed, it goes
					// first.
					// Desc (At least in all cases so far I've tested) doesn't
					// need the name
					// Owner's renaming requires nothing else.
					FieldInsnNode fin = (FieldInsnNode) ain;
					fin.name = updateFieldName(fin);
					fin.desc = updateFieldDesc(fin.desc);
					fin.owner = updateClassName(fin.owner);
				} else if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
					// Name relies on the desc and owner to be renamed, it goes
					// first.
					// Desc (At least in all cases so far I've tested) doesn't
					// need the name
					// Owner's renaming requires nothing else.
					MethodInsnNode min = (MethodInsnNode) ain;
					min.name = updateMethodName(min);
					min.desc = updateMethodDesc(min.desc);
					min.owner = updateClassName(min.owner);
				} else if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
					// Rename desc.
					TypeInsnNode tin = (TypeInsnNode) ain;
					tin.desc = updateClassName(tin.desc);
				} else if (ain.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
					InvokeDynamicInsnNode dynm = (InvokeDynamicInsnNode) ain;
					// TODO: How to go about renaming the name? Owner isn't
					// accessible.
					dynm.desc = updateMethodDesc(dynm.desc);
				} else if (ain.getType() == AbstractInsnNode.FRAME) {
					// Frames contain references to the original names. Until I
					// find a way to replace them I'm just gonna kill them.
					mn.instructions.set(ain, new InsnNode(Opcodes.NOP));
				}
			}
		}
		// Iterate fields. Update name and desc if needed.
		for (FieldNode fn : cn.fields) {
			fn.name = updateFieldName(cn, fn);
			if (needsRenaming(fn.desc)) {
				fn.desc = updateFieldDesc(fn.desc);
			}
		}
		// Rename the super class if needed.
		if (needsRenaming(cn.superName)) {
			cn.superName = updateClassName(cn.superName);
		}
	}

	/**
	 * Takes in a FieldInsnNode and returns the name dictated by the
	 * "RemappedName" annotation if one is present.
	 * 
	 * @param fin
	 * @return
	 */
	private String updateFieldName(FieldInsnNode fin) {
		ClassNode fieldOwner = getClassNode(fin.owner);
		while (!fieldOwner.name.equals("java/lang/Object")) {
			FieldNode field = getField(fieldOwner, fin.name, fin.desc);
			if (field != null) {
				return getRefactoredName(field);
			}
			fieldOwner = getClassNode(fieldOwner.superName);
		}
		return fin.name;
	}

	/**
	 * Takes in a FieldNode & ClassNode and returns the name dictated by the
	 * "RemappedName" annotation if one is present.
	 * 
	 * @param fieldOwner
	 * @param fin
	 * @return
	 */
	private String updateFieldName(ClassNode fieldOwner, FieldNode fin) {
		while (!fieldOwner.name.equals("java/lang/Object")) {
			FieldNode field = getField(fieldOwner, fin.name, fin.desc);
			if (field != null) {
				return getRefactoredName(field);
			}
			fieldOwner = getClassNode(fieldOwner.superName);
		}
		return fin.name;
	}

	/**
	 * Takes in a MethodInsnNode and returns the name dictated by the
	 * "RemappedName" annotation if one is present.
	 * 
	 * @param fin
	 * @return
	 */
	private String updateMethodName(MethodInsnNode min) {
		ClassNode methodOwner = getClassNode(min.owner);
		while (!methodOwner.name.equals("java/lang/Object")) {
			MethodNode method = getMethod(methodOwner, min.name, min.desc);
			if (method != null) {
				String renamed = getRefactoredName(method);
				if (!renamed.equals(min.name)) {
					return renamed;
				}
			}
			methodOwner = getClassNode(methodOwner.superName);
		}
		return min.name;
	}

	/**
	 * Takes in a MethodNode & ClassNode and returns the name dictated by the
	 * "RemappedName" annotation if one is present.
	 * 
	 * @param methodOwner
	 * @param mn
	 * @return
	 */
	private String updateMethodName(ClassNode methodOwner, MethodNode mn) {
		while (!methodOwner.name.equals("java/lang/Object")) {
			MethodNode method = getMethod(methodOwner, mn.name, mn.desc);
			if (method != null) {
				String renamed = getRefactoredName(method);
				if (!renamed.equals(mn.name)) {
					return renamed;
				}
			}
			methodOwner = getClassNode(methodOwner.superName);
		}
		return mn.name;
	}

	/**
	 * Updates a class's name. Used only when a ClassNode isn't directly
	 * available <i>(Which would allow getRefactoredName(ClassNode))</i>.
	 * 
	 * @param name
	 * @return
	 */
	private String updateClassName(String name) {
		ClassNode cn = getClassNode(name);
		if (cn != null) {
			return getRefactoredName(cn);
		}
		return name;
	}

	/**
	 * Updates a field's desc.
	 * 
	 * @param desc
	 * @return
	 */
	private String updateFieldDesc(String desc) {
		String fieldClass = Type.getType(desc).getClassName().replace(".", "/");
		if (needsRenaming(fieldClass)) {
			ClassNode fcn = getClassNode(fieldClass);
			desc = desc.replace(fcn.name, getRefactoredName(fcn));
		}
		return desc;
	}

	/**
	 * Updates a method's desc.
	 * 
	 * @param desc
	 * @return
	 */
	private String updateMethodDesc(String desc) {
		Type methodType = Type.getMethodType(desc);
		// Iterate args and replace with corrected names.
		for (Type arg : methodType.getArgumentTypes()) {
			String argClass = arg.getClassName().replace(".", "/");
			if (needsRenaming(argClass)) {
				ClassNode acn = getClassNode(argClass);
				desc = desc.replace(argClass, getRefactoredName(acn));
			}
		}
		// Replace return type.
		String returnClass = methodType.getReturnType().getClassName().replace(".", "/");
		if (needsRenaming(returnClass)) {
			ClassNode retcn = getClassNode(returnClass);
			desc = desc.replace(returnClass, getRefactoredName(retcn));
		}
		return desc;
	}

	/**
	 * Finds a field in a given ClassNode matching a given name and desc.
	 * 
	 * @param cn
	 * @param name
	 * @param desc
	 * @return
	 */
	private FieldNode getField(ClassNode cn, String name, String desc) {
		for (FieldNode fn : cn.fields) {
			if (fn.name.equals(name) && fn.desc.equals(desc)) {
				return fn;
			}
		}
		return null;
	}

	/**
	 * Finds a method in a given ClassNode matching a given name and desc.
	 * 
	 * @param cn
	 * @param name
	 * @param desc
	 * @return
	 */
	private MethodNode getMethod(ClassNode cn, String name, String desc) {
		for (MethodNode mn : cn.methods) {
			if (mn.name.equals(name) && mn.desc.equals(desc)) {
				return mn;
			}
		}
		return null;
	}

	/**
	 * Gets the name of a ClassNode dictated by the "RemappedName" annotation if
	 * one is present.
	 * 
	 * @param cn
	 * @return
	 */
	private String getRefactoredName(ClassNode cn) {
		if (cn.visibleAnnotations != null) {
			for (AnnotationNode an : cn.visibleAnnotations) {
				if (an.desc.equals(ANNO_DESC)) {
					return an.values.get(1).toString();
				}
			}
		}
		return cn.name;
	}

	/**
	 * Gets the name of a FieldNode dictated by the "RemappedName" annotation if
	 * one is present.
	 * 
	 * @param fn
	 * @return
	 */
	private String getRefactoredName(FieldNode fn) {
		if (fn.visibleAnnotations != null) {
			for (AnnotationNode an : fn.visibleAnnotations) {
				if (an.desc.equals(ANNO_DESC)) {
					return an.values.get(1).toString();
				}
			}
		}
		return fn.name;
	}

	/**
	 * Gets the name of a MethodNode dictated by the "RemappedName" annotation
	 * if one is present.
	 * 
	 * @param mn
	 * @return
	 */
	private String getRefactoredName(MethodNode mn) {
		try {
			if (mn.visibleAnnotations != null) {
				for (AnnotationNode an : mn.visibleAnnotations) {
					if (an.desc.equals(ANNO_DESC)) {
						String s = an.values.get(1).toString();
						return s;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return mn.name;
	}

	/**
	 * Checks if a string <i>(May be a name or desc)</i> needs updating.
	 * 
	 * @param name
	 * @return
	 */
	private boolean needsRenaming(String name) {
		return name.contains(shellPkg);
	}

	/**
	 * Instead of reloading the ClassNodes just store them for later.
	 */
	private static Map<String, ClassNode> cache =  new HashMap<String, ClassNode>();

	/**
	 * Loads a ClassNode given a internal name. <br>
	 * Example of an internal name: <i>"com/example/ExampleClass"</i>
	 * 
	 * @param internalName
	 * @return
	 */
	private ClassNode getClassNode(String internalName) {
		if (cache.containsKey(internalName)) {
			return cache.get(internalName);
		}
		try {
			ClassReader cr = new ClassReader(internalName);
			ClassNode cn = ASMUtils.getNode(cr.b);
			cache.put(cn.name, cn);

			return cn;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}