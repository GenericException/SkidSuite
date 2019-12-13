package me.lpk.gui.tabs.internal.handle;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;

public class LookupHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		LookupSwitchInsnNode lsin = (LookupSwitchInsnNode) ain;
		// TODO: Show more info about the labels and keys
		return node(ain) + "|" + lsin.labels.size() + "|" + lsin.keys.size() + "\n";
	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (asNode(inuput).getOpcode()) {
		case Opcodes.LOOKUPSWITCH:
		}
		return null;
	}
}
