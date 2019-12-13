package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.tree.AbstractInsnNode;

import me.lpk.util.OpUtil;

public abstract class NodeHandler {
	public abstract String asText(AbstractInsnNode ain);

	public abstract AbstractInsnNode asNode(String inuput);

	protected final String node(AbstractInsnNode ain) {
		return OpUtil.opcodes.get(ain.getOpcode());
	}

	protected final int opcode(String in) {
		return OpUtil.reopcodes.get(in);
	}
}
