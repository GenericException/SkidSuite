package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

public class MultiArrayHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		MultiANewArrayInsnNode manain = (MultiANewArrayInsnNode) ain;
		return node(ain) + "|" + manain.desc + "|" + manain.desc + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.MULTIANEWARRAY:
		}
		return null;
	}
}
