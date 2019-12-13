package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class VarHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		VarInsnNode vin = (VarInsnNode) ain;
		return node(ain) + "|" + vin.var + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.ILOAD:
		case Opcodes.LLOAD:
		case Opcodes.FLOAD:
		case Opcodes.DLOAD:
		case Opcodes.ALOAD:
		case Opcodes.ISTORE:
		case Opcodes.LSTORE:
		case Opcodes.FSTORE:
		case Opcodes.DSTORE:
		case Opcodes.ASTORE:
		case Opcodes.RET:
		}
		return null;
	}
}
