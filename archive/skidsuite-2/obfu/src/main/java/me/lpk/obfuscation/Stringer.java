package me.lpk.obfuscation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.lpk.util.AccessHelper;
import me.lpk.util.OpUtils;

public class Stringer {
	/**
	 * 
	 * @param cn
	 */
	public static void stringEncrypt(ClassNode cn) {
		Map<String, Integer> randInts = new HashMap<String, Integer>();
		Map<String, String> stringOldAndNew = new HashMap<String, String>();
		Set<String> strings = new HashSet<String>();
		String key = genKey(15);
		for (MethodNode mn : cn.methods) {
			for (String s : getStrings(mn)) {
				if (s.length() > 0) {
					strings.add(s);
				}
			}
		}
		if (strings.size() == 0) {
			return;
		}
		MethodNode decrypt = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, genKey(30), "([CLjava/lang/String;I)Ljava/lang/String;", null, null);
		createDecryptMethod(decrypt, true);
		cn.methods.add(decrypt);
		for (String oldText : strings) {
			int hash = oldText.hashCode();
			if (hash < 0) {
				hash *= -1;
			}
			int index = 0;
			int randInt = (int) (240 - Math.random() * 120);
			randInts.put(oldText, randInt);
			char[] obfuChars = new char[oldText.length()];
			for (char inChar : oldText.toCharArray()) {
				char nameChar = key.charAt(index % key.length());
				char cNew = (char) (((int) inChar + (int) nameChar) - randInt);
				obfuChars[index] = cNew;
				index++;
			}
			String obfuText = new String(obfuChars);
			stringOldAndNew.put(oldText, obfuText);
		}
		for (MethodNode mn : cn.methods) {
			for (AbstractInsnNode ain : mn.instructions.toArray()) {
				if (ain.getType() != AbstractInsnNode.LDC_INSN) {
					continue;
				}
				LdcInsnNode ldc = (LdcInsnNode) ain;
				if (!(ldc.cst instanceof String)) {
					continue;
				}
				String s = ldc.cst.toString();
				if (s.length() == 0) {
					continue;
				}
				AbstractInsnNode x = ain.getNext();

				// If the decrypt isn't static it needs the 'this' instance.
				if (!AccessHelper.isStatic(decrypt.access)) {
					mn.instructions.insertBefore(x, new VarInsnNode(Opcodes.ALOAD, 0));
				}
				// Create the char array
				mn.instructions.insertBefore(x, OpUtils.toInt(s.length()));
				mn.instructions.insertBefore(x, new IntInsnNode(Opcodes.NEWARRAY, 5));
				mn.instructions.insertBefore(x, new InsnNode(Opcodes.DUP));
				char[] arrr = stringOldAndNew.get(s).toCharArray();
				for (int ind = 0; ind < arrr.length; ind++) {
					char c = arrr[ind];
					int ic = (int) c;
					mn.instructions.insertBefore(x, OpUtils.toInt(ind));
					mn.instructions.insertBefore(x, OpUtils.toInt(ic));
					mn.instructions.insertBefore(x, new InsnNode(Opcodes.CASTORE));
					if (ind != arrr.length - 1) {
						mn.instructions.insertBefore(x, new InsnNode(Opcodes.DUP));
					}
				}

				// Add the key string
				mn.instructions.insertBefore(x, new LdcInsnNode(key));
				// Get the key int
				mn.instructions.insertBefore(x, OpUtils.toInt(randInts.get(s)));
				// Invoke decrypt
				if (AccessHelper.isStatic(decrypt.access)) {
					mn.instructions.insertBefore(x, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, decrypt.name, decrypt.desc, false));
				} else {
					mn.instructions.insertBefore(x, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, cn.name, decrypt.name, decrypt.desc, false));
				}
				// Remove the ldc
				mn.instructions.remove(ldc);
			}
		}
	}

	/**
	 * Creates a decrypt method in a given method node.
	 * 
	 * @param mn
	 */
	private static void createDecryptMethod(MethodNode mn, boolean isStatic) {
		if (isStatic) {
			mn.visitCode();
			mn.visitInsn(Opcodes.ICONST_0);
			mn.visitVarInsn(Opcodes.ISTORE, 3);
			mn.visitInsn(Opcodes.ICONST_0);
			mn.visitVarInsn(Opcodes.ISTORE, 4);
			Label lbl0 = new Label();
			mn.visitJumpInsn(Opcodes.GOTO, lbl0);
			Label lbl1 = new Label();
			mn.visitLabel(lbl1);
			mn.visitFrame(Opcodes.F_APPEND, 2, new Object[] { Opcodes.INTEGER, Opcodes.INTEGER }, 0, null);
			mn.visitVarInsn(Opcodes.ALOAD, 0);
			mn.visitVarInsn(Opcodes.ILOAD, 4);
			mn.visitInsn(Opcodes.CALOAD);
			mn.visitVarInsn(Opcodes.ISTORE, 5);
			mn.visitVarInsn(Opcodes.ALOAD, 1);
			mn.visitVarInsn(Opcodes.ILOAD, 3);
			mn.visitVarInsn(Opcodes.ALOAD, 1);
			mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
			mn.visitInsn(Opcodes.IREM);
			mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
			mn.visitVarInsn(Opcodes.ISTORE, 6);
			mn.visitVarInsn(Opcodes.ILOAD, 5);
			mn.visitVarInsn(Opcodes.ILOAD, 2);
			mn.visitInsn(Opcodes.IADD);
			mn.visitVarInsn(Opcodes.ILOAD, 6);
			mn.visitInsn(Opcodes.ISUB);
			mn.visitInsn(Opcodes.I2C);
			mn.visitVarInsn(Opcodes.ISTORE, 7);
			mn.visitVarInsn(Opcodes.ALOAD, 0);
			mn.visitVarInsn(Opcodes.ILOAD, 4);
			mn.visitVarInsn(Opcodes.ILOAD, 7);
			mn.visitInsn(Opcodes.CASTORE);
			mn.visitIincInsn(3, 1);
			mn.visitIincInsn(4, 1);
			mn.visitLabel(lbl0);
			mn.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mn.visitVarInsn(Opcodes.ILOAD, 4);
			mn.visitVarInsn(Opcodes.ALOAD, 0);
			mn.visitInsn(Opcodes.ARRAYLENGTH);
			mn.visitJumpInsn(Opcodes.IF_ICMPLT, lbl1);
			mn.visitTypeInsn(Opcodes.NEW, "java/lang/String");
			mn.visitInsn(Opcodes.DUP);
			mn.visitVarInsn(Opcodes.ALOAD, 0);
			mn.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
			mn.visitInsn(Opcodes.ARETURN);
			mn.visitMaxs(3, 8);
			mn.visitEnd();
		} else {
			mn.visitCode();
			mn.visitInsn(Opcodes.ICONST_0);
			mn.visitVarInsn(Opcodes.ISTORE, 4);
			mn.visitInsn(Opcodes.ICONST_0);
			mn.visitVarInsn(Opcodes.ISTORE, 5);
			Label l0 = new Label();
			mn.visitJumpInsn(Opcodes.GOTO, l0);
			Label l1 = new Label();
			mn.visitLabel(l1);
			mn.visitFrame(Opcodes.F_APPEND, 2, new Object[] { Opcodes.INTEGER, Opcodes.INTEGER }, 0, null);
			mn.visitVarInsn(Opcodes.ALOAD, 1);
			mn.visitVarInsn(Opcodes.ILOAD, 5);
			mn.visitInsn(Opcodes.CALOAD);
			mn.visitVarInsn(Opcodes.ISTORE, 6);
			mn.visitVarInsn(Opcodes.ALOAD, 2);
			mn.visitVarInsn(Opcodes.ILOAD, 4);
			mn.visitVarInsn(Opcodes.ALOAD, 2);
			mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
			mn.visitInsn(Opcodes.IREM);
			mn.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
			mn.visitVarInsn(Opcodes.ISTORE, 7);
			mn.visitVarInsn(Opcodes.ILOAD, 6);
			mn.visitVarInsn(Opcodes.ILOAD, 3);
			mn.visitInsn(Opcodes.IADD);
			mn.visitVarInsn(Opcodes.ILOAD, 7);
			mn.visitInsn(Opcodes.ISUB);
			mn.visitInsn(Opcodes.I2C);
			mn.visitVarInsn(Opcodes.ISTORE, 8);
			mn.visitVarInsn(Opcodes.ALOAD, 1);
			mn.visitVarInsn(Opcodes.ILOAD, 5);
			mn.visitVarInsn(Opcodes.ILOAD, 8);
			mn.visitInsn(Opcodes.CASTORE);
			mn.visitIincInsn(4, 1);
			mn.visitIincInsn(5, 1);
			mn.visitLabel(l0);
			mn.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mn.visitVarInsn(Opcodes.ILOAD, 5);
			mn.visitVarInsn(Opcodes.ALOAD, 1);
			mn.visitInsn(Opcodes.ARRAYLENGTH);
			mn.visitJumpInsn(Opcodes.IF_ICMPLT, l1);
			mn.visitTypeInsn(Opcodes.NEW, "java/lang/String");
			mn.visitInsn(Opcodes.DUP);
			mn.visitVarInsn(Opcodes.ALOAD, 1);
			mn.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
			mn.visitInsn(Opcodes.ARETURN);
			mn.visitMaxs(3, 9);
			mn.visitEnd();
		}
	}

	public static String decrypt(char[] input, String key, int randInt) {
		int index = 0;
		for (int i = 0; i < input.length; i++) {
			char inChar = input[i];
			char nameChar = key.charAt(index % key.length());
			char newChar = (char) ((int) inChar + randInt - (int) nameChar);
			input[i] = newChar;
			index++;
		}
		return new String(input);
	}

	/**
	 * Gets all strings in a method node.
	 * 
	 * @param mn
	 * @return
	 */
	public static List<String> getStrings(MethodNode mn) {
		List<String> list = new ArrayList<String>();
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() != AbstractInsnNode.LDC_INSN) {
				continue;
			}
			LdcInsnNode ldc = (LdcInsnNode) ain;
			if (!(ldc.cst instanceof String)) {
				continue;
			}
			String s = ldc.cst.toString();
			if (!list.contains(s)) {
				list.add(s);
			}
		}
		return list;
	}
	
	public static String genKey(int len) {
		return genKey(len, 600,900);
	}
	
	public static String genKey(int len, int rangeMin, int rangeMax) {
		String s = "";
		while (s.length() < len) {
			s = s + ((char) (rangeMin +( Math.random() * (rangeMax - rangeMin))));
		}
		return s;
	}

}
