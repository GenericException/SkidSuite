package me.lpk.asm.deob.method;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.asm.AbstractMethodTransformer;

public class AllatoriMethodTransformer extends AbstractMethodTransformer {

	public AllatoriMethodTransformer(ClassNode node) {
		super( node);
	}

	@Override
	public void transform(MethodNode method) {
		// TODO: Figure out why SimpleStringTransformer does not work on
		// Allatori obfuscated text calls.
	}
}