package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		FieldInsnNode fin = (FieldInsnNode) ain;
		return node(ain) + "|" + fin.name + "|" + fin.owner + "|" + fin.desc + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.GETSTATIC:
		case Opcodes.PUTSTATIC:
		case Opcodes.GETFIELD:
		case Opcodes.PUTFIELD:
		}
		return null;
	}
}
