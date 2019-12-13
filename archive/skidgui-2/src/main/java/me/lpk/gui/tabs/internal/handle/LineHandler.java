package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

public class LineHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		LineNumberNode lnn = (LineNumberNode) ain;
		return node(ain) + "|" + lnn.line + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		// Since opcode is always -1 no need to switch statement
		return null;
	}
}
