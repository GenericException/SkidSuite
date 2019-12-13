package me.lpk.obfuscation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.lpk.util.AccessHelper;
import me.lpk.util.OpUtils;

public class Flow {

	public static void randomGotos(MethodNode mn) {
		if (mn.name.startsWith("<") || AccessHelper.isAbstract(mn.access)) {
			return;
		}
		int instructs = mn.instructions.size();
		if (instructs < 4) {
			// Too short, don't bother.
			return;
		}
		int min = 1;
		int max = Math.max(min, instructs - 2);
		Random r = new Random();
		int randCut = (int) (min + (r.nextDouble() * (max - min)));
		AbstractInsnNode ain = mn.instructions.get(randCut);
		LabelNode labelAfter = new LabelNode();
		LabelNode labelBefore = new LabelNode();
		LabelNode labelFinal = new LabelNode();
		mn.instructions.insertBefore(ain, labelBefore);
		mn.instructions.insert(ain, labelAfter);
		mn.instructions.insert(labelAfter, labelFinal);
		// TODO: Add variety. Make opaque predicates
		// Tried before but muh stackframes
		mn.instructions.insertBefore(labelBefore, new JumpInsnNode(Opcodes.GOTO, labelAfter));
		mn.instructions.insertBefore(labelAfter, new JumpInsnNode(Opcodes.GOTO, labelFinal));
		mn.instructions.insertBefore(labelFinal, new JumpInsnNode(Opcodes.GOTO, labelBefore));
	}

	public static void addTryCatch(MethodNode mn, String catchType, String handleType) {
		if (mn.name.startsWith("<") || AccessHelper.isAbstract(mn.access)) {
			return;
		}
		LabelNode start = new LabelNode();
		LabelNode handler = new LabelNode();
		LabelNode end = new LabelNode();
		if (mn.localVariables == null) {
			mn.localVariables = new ArrayList<LocalVariableNode>(5);
		}
		int index = mn.localVariables.size();
		mn.instructions.insert(start);
		mn.instructions.add(end);
		mn.instructions.add(handler);
		mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
		mn.instructions.add(new InsnNode(Opcodes.ATHROW));

		LocalVariableNode exVar = new LocalVariableNode("excptn", "L" + catchType + ";", null, start, handler, index);
		TryCatchBlockNode tryBlock = new TryCatchBlockNode(start, end, handler, handleType == null ? null : ("L" + handleType + ";"));
		mn.localVariables.add(exVar);
		mn.tryCatchBlocks.add(tryBlock);
		mn.exceptions.add(catchType);
	}

	/**
	 * Merges private fields.
	 * 
	 * @param cn
	 */
	public static void mergeFields(ClassNode cn) {
		// Get private fields
		List<FieldNode> fields = new ArrayList<FieldNode>();
		for (FieldNode fn : cn.fields) {
			if (AccessHelper.isPrivate(fn.access) && AccessHelper.isStatic(fn.access)) {
				fields.add(fn);
			}
		}
		if (fields.size() == 0) {
			return;
		}
		FieldNode merged = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "_MERGE_", "Ljava/util/List;", null, null);
		// Create getter/setter methods for fields
		// TODO: Rewrite so it's only one getter and one setter
		// Type casting will be inline, not at the g/s
		List<MethodNode> getter = new ArrayList<MethodNode>();
		List<MethodNode> setter = new ArrayList<MethodNode>();
		for (FieldNode fn : fields) {
			int i = fields.indexOf(fn);
			MethodNode get = new MethodNode(Opcodes.ACC_STATIC, "get" + i, "()Ljava/lang/Object;", null, null);
			get.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, merged.name, merged.desc));
			get.instructions.add(OpUtils.toInt(i));
			get.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true));
			get.instructions.add(new InsnNode(Opcodes.ARETURN));
	
			/*
			 * if (fn.desc.length() == 1){ switch (fn.desc){ case "I": case "Z":
			 * get.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
			 * "Ljava/lang/Integer;")); get.instructions.add(new
			 * MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer",
			 * "intValue", "()I", false)); get.instructions.add(new
			 * InsnNode(Opcodes.IRETURN)); break; case "L":
			 * get.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
			 * "Ljava/lang/Long;")); get.instructions.add(new
			 * MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Long",
			 * "longValue", "()L", false)); get.instructions.add(new
			 * InsnNode(Opcodes.LRETURN)); break; case "D":
			 * get.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
			 * "Ljava/lang/Double;")); get.instructions.add(new
			 * MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double",
			 * "doubleValue", "()D", false)); get.instructions.add(new
			 * InsnNode(Opcodes.DRETURN)); break; case "F":
			 * get.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
			 * "Ljava/lang/Float;")); get.instructions.add(new
			 * MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Float",
			 * "floatValue", "()F", false)); get.instructions.add(new
			 * InsnNode(Opcodes.FRETURN)); break; } }else{
			 * get.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST,
			 * fn.desc)); get.instructions.add(new InsnNode(Opcodes.ARETURN)); }
			 */
	
			MethodNode set = new MethodNode(Opcodes.ACC_STATIC, "set" + i, "(Ljava/lang/Object;)V", null, null);
			set.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, merged.name, merged.desc));
			set.instructions.add(OpUtils.toInt(i));
			set.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			set.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(ILjava/lang/Object;)V", true));
			set.instructions.add(new InsnNode(Opcodes.RETURN));
	
			getter.add(i, get);
			setter.add(i, set);
			cn.methods.add(get);
			cn.methods.add(set);
		}
		// Get static
		MethodNode clinit = null;
		for (MethodNode mn : cn.methods) {
			if (mn.name.equals("<clinit>")) {
				clinit = mn;
			}
		}
		if (clinit == null) {
			clinit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
			clinit.instructions.add(new InsnNode(Opcodes.RETURN));
			cn.methods.add(clinit);
	
		}
		// Create map in static block
		clinit.instructions.insert(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, merged.name, merged.desc));
		clinit.instructions.insert(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
		clinit.instructions.insert(new InsnNode(Opcodes.DUP));
		clinit.instructions.insert(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
		cn.fields.add(merged);
		// Remove fields
		for (FieldNode fn : fields) {
			cn.fields.remove(cn.fields.indexOf(fn));
		}
		// Iterate methods for field interaction
		for (MethodNode mn : cn.methods) {
			for (FieldNode fn : fields) {
				int i = fields.indexOf(fn);
				for (AbstractInsnNode ain : mn.instructions.toArray()) {
					if (ain.getType() == AbstractInsnNode.FIELD_INSN) {
						FieldInsnNode fin = (FieldInsnNode) ain;
						if (fin.name.equals(fn.name) && fin.desc.equals(fn.desc) && fin.owner.equals(cn.name)) {
							MethodNode set = setter.get(i);
							MethodNode get = getter.get(i);
							if (fin.getOpcode() == Opcodes.PUTSTATIC) {
								mn.instructions.set(fin, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, set.name, set.desc, false));
							}
							if (fin.getOpcode() == Opcodes.GETSTATIC) {
								if (fn.desc.length() == 1) {
									switch (fn.desc) {
									case "Z":
										mn.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false));
										mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, "Ljava/lang/Boolean;"));
										break;
									case "I":
										mn.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
										mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, "Ljava/lang/Integer;"));
										break;
									case "L":
										mn.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()L", false));
										mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, "Ljava/lang/Long;"));
										break;
									case "D":
										mn.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false));
										mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, "Ljava/lang/Double;"));
										break;
									case "F":
										mn.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false));
										mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, "Ljava/lang/Float;"));
										break;
									case "C":
										mn.instructions.insert(fin, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false));
										mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, "Ljava/lang/Character;"));
										break;
									}
								} else {
									mn.instructions.insert(fin, new TypeInsnNode(Opcodes.CHECKCAST, fn.desc));
								}
								mn.instructions.set(fin, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, get.name, get.desc, false));
							}
						}
					}
				}
			}
		}
	}

	
}
