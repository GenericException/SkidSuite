package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		MethodInsnNode min = (MethodInsnNode) ain;
		return node(ain) + "|" + min.name + "|" + min.owner + "|" + min.desc + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.INVOKEVIRTUAL:
		case Opcodes.INVOKESPECIAL:
		case Opcodes.INVOKESTATIC:
		case Opcodes.INVOKEINTERFACE:
		}
		return null;
	}
}
