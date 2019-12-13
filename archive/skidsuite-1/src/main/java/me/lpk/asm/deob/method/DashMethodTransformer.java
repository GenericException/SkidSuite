package me.lpk.asm.deob.method;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import me.lpk.asm.deob.DeobfuscationVisitor;
import me.lpk.util.OpUtil;

public class DashMethodTransformer extends DeobTransformer {

	public DashMethodTransformer(DeobfuscationVisitor dv, ClassNode node) {
		super(dv, node);
	}

	@Override
	public void transform(MethodNode method) {
		for (AbstractInsnNode ain : method.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.LDC_INSN) {
				if (ain.getNext() == null || ain.getNext().getNext() == null) {
					continue;
				}
				AbstractInsnNode next = ain.getNext();
				while (next.getOpcode() == Opcodes.NOP) {
					next = next.getNext();
					if (next == null) {
						break;
					}
				}
				if (next == null || next.getNext() == null) {
					continue;
				}
				int nextOp = next.getOpcode();
				boolean isNextInt = (nextOp >= Opcodes.ICONST_0 && nextOp <= Opcodes.ICONST_5) || nextOp == Opcodes.BIPUSH || nextOp == Opcodes.SIPUSH;
				boolean isNextNextMethod = next.getNext().getType() == AbstractInsnNode.METHOD_INSN;
				if (!isNextInt || !isNextNextMethod){
					continue;
				}
				
				MethodInsnNode min = (MethodInsnNode) next.getNext();
				if (min.desc.equals("(Ljava/lang/String;I)Ljava/lang/String;")) {
					LdcInsnNode ldc = (LdcInsnNode) ain;
					int num = OpUtil.getIntValue(next);
					method.instructions.set(ain, new LdcInsnNode(decrypt((String) ldc.cst, num)));
					method.instructions.set(next.getNext(), new InsnNode(Opcodes.NOP));
					method.instructions.set(next, new InsnNode(Opcodes.NOP));
				}
			}
		}
	}

	/**
	 * Copy pasted from DashO. TODO: Account for other DashO varients
	 * 
	 * @param input
	 * @param modifier
	 * @return
	 */
	public static String decrypt(final String s, int n) {
		final int n2 = 4;
		final int n3 = n2 + 1;
		final boolean b = false;
		final char[] charArray = s.toCharArray();
		final int length = charArray.length;
		final char[] array = charArray;
		int n4 = b ? 1 : 0;
		final int n5 = (n2 << n3) - 1 ^ 0x20;
		char[] array2;
		while (true) {
			array2 = array;
			if (n4 == length) {
				break;
			}
			final int n6 = n4;
			final int n7 = (n & n5) ^ array2[n6];
			++n;
			++n4;
			array2[n6] = (char) n7;
		}
		return String.valueOf(array2, 0, length).intern();
	}
}