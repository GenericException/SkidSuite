package me.lpk.gui.tabs.internal.handle;import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
public class IntHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		IntInsnNode iin = (IntInsnNode) ain;
		return node(ain) + "|" + iin.operand + "\n";	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.BIPUSH:
		case Opcodes.SIPUSH:
		case Opcodes.NEWARRAY:
		}
		return null;
	}
}
