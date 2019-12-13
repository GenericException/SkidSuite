package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;

public class FrameHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		FrameNode fn = (FrameNode) ain;
		return node(ain) + "|" + fn.type + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.F_NEW:
		case Opcodes.F_FULL:
		case Opcodes.F_APPEND:
		case Opcodes.F_CHOP:
		case Opcodes.F_SAME:
		case Opcodes.F_SAME1:
		}
		return null;
	}
}
