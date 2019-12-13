package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

public class JumpHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		JumpInsnNode jin = (JumpInsnNode) ain;
		return node(ain) + "|" + jin.label.getLabel().line + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.IFEQ:
		case Opcodes.IFNE:
		case Opcodes.IFLT:
		case Opcodes.IFGE:
		case Opcodes.IFGT:
		case Opcodes.IFLE:
		case Opcodes.IF_ICMPEQ:
		case Opcodes.IF_ICMPNE:
		case Opcodes.IF_ICMPLT:
		case Opcodes.IF_ICMPGE:
		case Opcodes.IF_ICMPGT:
		case Opcodes.IF_ICMPLE:
		case Opcodes.IF_ACMPEQ:
		case Opcodes.IF_ACMPNE:
		case Opcodes.GOTO:
		case Opcodes.JSR:
		case Opcodes.IFNULL:
		case Opcodes.IFNONNULL:
		}
		return null;
	}
}
