package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

public class SwitchHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		TableSwitchInsnNode tsin = (TableSwitchInsnNode) ain;
		// Do something with List<Labels>?
		return node(ain) + "|" + tsin.min + "-" + tsin.max + "|" + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.TABLESWITCH:
		}
		return null;
	}
}
