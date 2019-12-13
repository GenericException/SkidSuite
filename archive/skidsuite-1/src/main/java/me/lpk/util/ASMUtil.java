package me.lpk.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ASMUtil {
	/**
	 * Gets the bytes of a given ClassNode
	 * 
	 * @param cn
	 * @return
	 */
	public static byte[] getNodeBytes(ClassNode cn) {
		ClassWriter cw = new ClassWriter(0);
		cn.accept(cw);
		byte[] b = cw.toByteArray();
		return b;
	}

	/**
	 * Gets a ClassNode based on given bytes
	 * 
	 * @param bytez
	 * @return
	 */
	public static ClassNode getNode(final byte[] bytez) {
		ClassReader cr = new ClassReader(bytez);
		ClassNode cn = new ClassNode();
		try {
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
		} catch (Exception e) {
			try {
				cr.accept(cn, ClassReader.SKIP_FRAMES);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		cr = null;
		return cn;
	}
}
