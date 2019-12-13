package me.lpk.asm.deob.method;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.asm.deob.DeobfuscationVisitor;

public abstract class DeobTransformer extends AbstractMethodTransformer {
	protected final DeobfuscationVisitor dv;

	public DeobTransformer(DeobfuscationVisitor dv, ClassNode node) {
		super(node);
		this.dv = dv;
	}

}
