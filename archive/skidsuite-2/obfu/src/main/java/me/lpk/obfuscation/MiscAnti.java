package me.lpk.obfuscation;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.lpk.util.AccessHelper;
import me.lpk.util.OpUtils;

public class MiscAnti {
	public static final String s = getMassive();

	/**
	 * Inserts a couple of massive LDC's into methods. Slows down javap.
	 * 
	 * @param mn
	 */
	public static void massiveLdc(MethodNode mn) {
		if (mn.name.contains("<") || AccessHelper.isAbstract(mn.access)) {
			return;
		}
		for (int i = 0; i < 3; i++) {
			mn.instructions.insert(new InsnNode(Opcodes.POP2));
			mn.instructions.insert(new LdcInsnNode(s));
			mn.instructions.insert(new InsnNode(Opcodes.POP));
			mn.instructions.insert(new InsnNode(Opcodes.SWAP));
			mn.instructions.insert(new InsnNode(Opcodes.POP));
			mn.instructions.insert(new LdcInsnNode(s));
			mn.instructions.insert(new LdcInsnNode(s));
			mn.instructions.insert(new LdcInsnNode(s));
		}
	}

	/**
	 * Duplicates vars. Uglifies opcodes / stack, does not seem to affect
	 * decompilation.
	 * 
	 * @param mn
	 */
	public static void duplicateVars(MethodNode mn) {
		if (AccessHelper.isAbstract(mn.access)) {
			return;
		}
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.VAR_INSN) {
				VarInsnNode vin = (VarInsnNode) ain;
				if (vin.getOpcode() == Opcodes.ASTORE) {
					mn.instructions.insertBefore(vin, new InsnNode(Opcodes.DUP));
					mn.instructions.insertBefore(vin, new InsnNode(Opcodes.ACONST_NULL));
					mn.instructions.insertBefore(vin, new InsnNode(Opcodes.SWAP));
					AbstractInsnNode next = vin.getNext();
					// Astore
					mn.instructions.insertBefore(next, new InsnNode(Opcodes.POP));
					mn.instructions.insertBefore(next, new VarInsnNode(Opcodes.ASTORE, vin.var));
				}
			}
		}
	}

	/**
	 * Messes with a few decompilers when used in combination with other
	 * features. Apparently POP2 is hard to work with.
	 * 
	 * @param mn
	 */
	public static void badPop(MethodNode mn) {
		if (AccessHelper.isAbstract(mn.access)) {
			return;
		}
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			int op = ain.getOpcode();
			if (op == Opcodes.ALOAD || op == Opcodes.ILOAD || op == Opcodes.FLOAD) {
				VarInsnNode vin = (VarInsnNode) ain;
				mn.instructions.insert(vin, new InsnNode(Opcodes.POP2));
				mn.instructions.insertBefore(vin, new VarInsnNode(op, vin.var));
				mn.instructions.insertBefore(vin, new VarInsnNode(op, vin.var));
			}
		}
	}

	/**
	 * Inserts a method that returns an object. Uses it as an exeption in code
	 * that is beyond the return operand (never executed). Makes Fernflower fail
	 * to decompile methods.
	 * 
	 * @param cn
	 */
	public static void retObjErr(ClassNode cn) {
		String mthdN = Stringer.genKey(10), mthdR = "()Ljava/lang/Object;";
		String catchType = "java/lang/Exception";
		for (MethodNode mn : cn.methods) {
			if (mn.name.contains("<") || AccessHelper.isAbstract(mn.access) || mn.instructions.size() < 4) {
				continue;
			}
			AbstractInsnNode last = mn.instructions.getLast();
			/*
			 * while (last.getType() == AbstractInsnNode.FRAME) { last =
			 * last.getPrevious(); }
			 */
			LabelNode beforeInvoke = new LabelNode();
			LabelNode afterInvoke = new LabelNode();
			LabelNode beforeAthrow = new LabelNode();
			LabelNode afterAthrow = new LabelNode();
			LabelNode handler = new LabelNode();
			if (mn.localVariables == null) {
				mn.localVariables = new ArrayList<LocalVariableNode>(5);
			}
			int index = mn.localVariables.size();
			LocalVariableNode exVarInvoke = new LocalVariableNode("excptnInvoke", "L" + catchType + ";", null, beforeInvoke, afterInvoke, index);
			LocalVariableNode exVarThrow = new LocalVariableNode("excptnThrow", "L" + catchType + ";", null, beforeAthrow, afterAthrow, index + 1);
			TryCatchBlockNode tryBlockInvoke = new TryCatchBlockNode(beforeInvoke, afterInvoke, handler, null);
			TryCatchBlockNode tryBlockThrow = new TryCatchBlockNode(beforeAthrow, afterAthrow, handler, null);
			mn.instructions.insertBefore(last, beforeInvoke);
			mn.instructions.insertBefore(last, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, mthdN, mthdR, false));
			mn.instructions.insertBefore(last, afterInvoke);
			mn.instructions.insertBefore(last, new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/NullPointerException"));
			mn.instructions.insertBefore(last, beforeAthrow);
			mn.instructions.insertBefore(last, new InsnNode(Opcodes.ATHROW));
			// mn.instructions.insertBefore(last, new InsnNode(Opcodes.POP));
			mn.instructions.insertBefore(last, afterAthrow);
			mn.instructions.insertBefore(last, handler);
			mn.tryCatchBlocks.add(tryBlockInvoke);
			mn.tryCatchBlocks.add(tryBlockThrow);
			mn.localVariables.add(exVarInvoke);
			mn.localVariables.add(exVarThrow);

			if (!mn.exceptions.contains(catchType)) {
				mn.exceptions.add(catchType);
			}
		}
		int acc = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
		MethodNode objMethod = new MethodNode(acc, mthdN, mthdR, null, new String[] {});
		objMethod.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/NullPointerException"));
		objMethod.instructions.add(new InsnNode(Opcodes.DUP));
		objMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "()V", false));
		objMethod.instructions.add(new InsnNode(Opcodes.ARETURN));
		cn.methods.add(objMethod);

	}

	/**
	 * Adds a try catch around an entire method.
	 * 
	 * @param mn
	 * @param catchType
	 * @param handleType
	 */
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
		mn.instructions.add(handler);
		mn.instructions.add(new InsnNode(Opcodes.NOP));
		mn.instructions.add(end);
		mn.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
		mn.instructions.add(new InsnNode(Opcodes.ATHROW));

		LocalVariableNode exVar = new LocalVariableNode("excptn", "L" + catchType + ";", null, start, handler, index);
		TryCatchBlockNode tryBlock = new TryCatchBlockNode(start, end, handler, handleType == null ? null : ("L" + handleType + ";"));
		mn.localVariables.add(exVar);
		mn.tryCatchBlocks.add(tryBlock);
		mn.exceptions.add(catchType);
	}

	/**
	 * Creates a long string.
	 * 
	 * @return
	 */
	private static String getMassive() {
		StringBuffer sb = new StringBuffer();
		while (sb.length() < 65536 - 1) {
			sb.append("z");
		}
		return sb.toString();
	}

	/**
	 * Removes the types of all local variables.
	 * 
	 * @param nodes
	 */
	public static void removeLocalTypes(Collection<ClassNode> nodes) {
		for (ClassNode cn : nodes) {
			for (MethodNode mn : cn.methods) {
				if (mn.localVariables != null) {
					for (LocalVariableNode lvn : mn.localVariables) {
						lvn.signature = null;
						//lvn.desc = "Ljava/lang/Object;";
					}
				}
			}
		}
	}

	/**
	 * Marks a collection of nodes and their members as synthetic.
	 * 
	 * @param nodes
	 */
	public static void makeSynthetic(Collection<ClassNode> nodes) {
		for (ClassNode cn : nodes) {
			if (!AccessHelper.isSynthetic(cn.access)) {
				cn.access |= Opcodes.ACC_SYNTHETIC;
			}
			for (FieldNode fn : cn.fields) {
				if (!AccessHelper.isSynthetic(fn.access)) {
					fn.access |= Opcodes.ACC_SYNTHETIC;
				}
			}
			for (MethodNode mn : cn.methods) {
				if (!AccessHelper.isSynthetic(mn.access)) {
					mn.access |= Opcodes.ACC_SYNTHETIC;
				}
			}
		}
	}

	/**
	 * Splits up math operations.
	 * 
	 * @param mn
	 */
	public static void breakMath(MethodNode mn) {
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.INT_INSN && ain.getOpcode() != Opcodes.NEWARRAY) {
				if (isNear(ain)) {
					continue;
				}
				IntInsnNode iin = (IntInsnNode) ain;
				int i = randRange(-100, 100);
				switch (randRange(0, 3)) {
				case 0:
					iin.operand += i;
					mn.instructions.insert(iin, new InsnNode(Opcodes.ISUB));
					mn.instructions.insert(iin, new InsnNode(Opcodes.SWAP));
					mn.instructions.insertBefore(iin, OpUtils.toInt(i));
					break;
				case 1:
					iin.operand -= i;
					mn.instructions.insert(iin, new InsnNode(Opcodes.IADD));
					mn.instructions.insert(iin, new InsnNode(Opcodes.SWAP));
					mn.instructions.insertBefore(iin, OpUtils.toInt(i));
					break;
				case 2:
					iin.operand += i;
					mn.instructions.insert(iin, new InsnNode(Opcodes.IADD));
					mn.instructions.insert(iin, new InsnNode(Opcodes.INEG));
					mn.instructions.insert(iin, new InsnNode(Opcodes.SWAP));
					mn.instructions.insertBefore(iin, OpUtils.toInt(i));
					break;
				case 3:
					iin.operand -= i;
					mn.instructions.insert(iin, new InsnNode(Opcodes.ISUB));
					mn.instructions.insert(iin, new InsnNode(Opcodes.INEG));
					mn.instructions.insert(iin, new InsnNode(Opcodes.SWAP));
					mn.instructions.insertBefore(iin, OpUtils.toInt(i));
					break;
				}
			}
		}
	}

	static int randRange(int min, int max) {
		return (int) (min + (Math.random() * (max - min)));
	}

	static boolean isNear(AbstractInsnNode ain) {
		AbstractInsnNode node = ain;
		int j = 3, ii = 0;
		while (j != 0 && node.getPrevious() != null) {
			node = node.getPrevious();
			j--;
			ii += 2;
		}
		int i = 0;
		while (i < ii && node.getNext() != null) {
			int o = node.getOpcode();
			if (o == Opcodes.AALOAD || o == Opcodes.AASTORE || o == Opcodes.ANEWARRAY || o == Opcodes.NEWARRAY || o == Opcodes.CASTORE) {
				return true;
			}
			node = node.getNext();
			i++;
		}
		return false;
	}
}
