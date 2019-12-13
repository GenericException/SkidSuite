package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		LdcInsnNode ldc = (LdcInsnNode) ain;
		return node(ain) + "|" + ldc.cst + "|" + ldc.cst.getClass().getName() + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.LDC:
		}
		return null;
	}
}
