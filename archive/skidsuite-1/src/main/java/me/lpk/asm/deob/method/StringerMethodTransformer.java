package me.lpk.asm.deob.method;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.asm.AbstractMethodTransformer;

public class StringerMethodTransformer extends AbstractMethodTransformer {

	public StringerMethodTransformer(ClassNode node) {
		super(node);
	}

	@Override
	public void transform(MethodNode method) {
		for (AbstractInsnNode ain : method.instructions.toArray()) {
			
		}
	}
}