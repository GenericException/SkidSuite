package me.lpk.gui.tabs.internal.handle;import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
public class IincHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		IincInsnNode iinc = (IincInsnNode) ain;
		return node(ain) + "|" + iinc.var + "|" + iinc.incr + "\n";	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.IINC:
		}
		return null;
	}
}
