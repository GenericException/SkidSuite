package me.lpk.gui.tabs.internal.handle;import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
public class TypeHandler extends NodeHandler {
	@Override
	public String asText(AbstractInsnNode ain) {
		TypeInsnNode tin = (TypeInsnNode) ain;
		return node(ain) + "|" + tin.desc + "\n";	}

	@Override
	public AbstractInsnNode asNode(String inuput) {
		switch (this.opcode(inuput)) {
		case Opcodes.NEW:
		case Opcodes.ANEWARRAY:
		case Opcodes.CHECKCAST:
		case Opcodes.INSTANCEOF:
		}
		return null;
	}
}
