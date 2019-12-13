package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class LabelHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		LabelNode ln = (LabelNode) ain;
		//TODO: Do more with the label info?
		return node(ain) + "|" + ln.getLabel().line + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		//Since opcode is always -1 no need to switch statement
		return null;
	}
}
