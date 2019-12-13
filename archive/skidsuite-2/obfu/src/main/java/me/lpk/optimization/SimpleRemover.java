package me.lpk.optimization;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import me.lpk.util.RegexUtils;

public class SimpleRemover extends Remover {
	private final Set<String> visited = new HashSet<String>();

	@Override
	public void getUsedClasses(String mainClass, Map<String, ClassNode> nodes) {
		ClassNode initNode = nodes.get(mainClass);
		if (initNode == null) {
			JOptionPane.showMessageDialog(null, "Main class '" + mainClass + "' was not detected. No removal will occur.", "Error", JOptionPane.ERROR_MESSAGE);
			keep.addAll(nodes.keySet());
		} else {
			keep.add(mainClass);
			keep.addAll(checkIsUsed(initNode, nodes));
		}
	}

	/**
	 * 
	 * @param node
	 * @param nodes
	 * @return
	 */
	private Set<String> checkIsUsed(ClassNode node, Map<String, ClassNode> nodes) {
		Set<String> keep = new HashSet<String>();
		visited.add(node.name);
		String parent = node.superName;
		if (parent != null) {
			keep.add(parent);
			if (!visited.contains(parent) && nodes.containsKey(parent)) {
				keep.addAll(checkIsUsed(nodes.get(parent), nodes));
			}
		}
		for (String name : node.interfaces) {
			keep.add(name);
			if (!visited.contains(name) && nodes.containsKey(name)) {
				keep.addAll(checkIsUsed(nodes.get(name), nodes));
			}
		}
		for (FieldNode fn : node.fields) {
			for (String name : RegexUtils.matchDescriptionClasses(fn.desc)) {
				keep.add(name);
				if (!visited.contains(name) && nodes.containsKey(name)) {
					keep.addAll(checkIsUsed(nodes.get(name), nodes));
				}
			}
		}
		for (MethodNode mn : node.methods) {
			for (String name : RegexUtils.matchDescriptionClasses(mn.desc)) {
				keep.add(name);
				if (!visited.contains(name) && nodes.containsKey(name)) {
					keep.addAll(checkIsUsed(nodes.get(name), nodes));
				}
			}
			for (AbstractInsnNode ain : mn.instructions.toArray()) {
				if (ain.getType() == AbstractInsnNode.FIELD_INSN) {
					FieldInsnNode fin = (FieldInsnNode) ain;
					for (String name : RegexUtils.matchDescriptionClasses(fin.desc)) {
						keep.add(name);
						if (!visited.contains(name) && nodes.containsKey(name)) {
							keep.addAll(checkIsUsed(nodes.get(name), nodes));
						}
					}
					keep.add(fin.owner);
					if (!visited.contains(fin.owner) && nodes.containsKey(fin.owner)) {
						keep.addAll(checkIsUsed(nodes.get(fin.owner), nodes));
					}
				} else if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
					MethodInsnNode min = (MethodInsnNode) ain;
					for (String name : RegexUtils.matchDescriptionClasses(min.desc)) {
						keep.add(name);
						if (!visited.contains(name) && nodes.containsKey(name)) {
							keep.addAll(checkIsUsed(nodes.get(name), nodes));
						}
					}
					keep.add(min.owner);
					if (!visited.contains(min.owner) && nodes.containsKey(min.owner)) {
						keep.addAll(checkIsUsed(nodes.get(min.owner), nodes));
					}
				} else if (ain.getType() == AbstractInsnNode.LDC_INSN) {
					LdcInsnNode ldc = (LdcInsnNode) ain;
					if (ldc.cst instanceof Type) {
						Type t = (Type) ldc.cst;
						String name = t.getClassName().replace(".", "/");
						keep.add(name);
						if (!visited.contains(name) && nodes.containsKey(name)) {
							keep.addAll(checkIsUsed(nodes.get(name), nodes));
						}
					}
				} else if (ain.getType() == AbstractInsnNode.TYPE_INSN) {
					TypeInsnNode tin = (TypeInsnNode) ain;
					for (String name : RegexUtils.matchDescriptionClasses(tin.desc)) {
						keep.add(name);
						if (!visited.contains(name) && nodes.containsKey(name)) {
							keep.addAll(checkIsUsed(nodes.get(name), nodes));
						}
					}
				}
			}
		}
		return keep;
	}

	@Override
	public boolean isMethodUsed(String className, String mthdKey) {
		return true;
	}

}
