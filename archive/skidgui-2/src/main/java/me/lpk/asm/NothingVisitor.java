package me.lpk.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * An instantiatable ClassVisitor. Does literally nothing.
 */
public class NothingVisitor extends ClassVisitor {
	public NothingVisitor(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}
}
