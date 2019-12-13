package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

public class InvokeDynHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		InvokeDynamicInsnNode idin = (InvokeDynamicInsnNode) ain;
		// TODO: idin.bsm;
		return node(ain) + "|" + idin.name + "|" + "|" + idin.desc + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.INVOKEDYNAMIC:
		}
		return null;
	}
}
