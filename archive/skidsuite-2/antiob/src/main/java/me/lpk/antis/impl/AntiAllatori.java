package me.lpk.antis.impl;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.analysis.Sandbox;
import me.lpk.antis.AntiBase;
import me.lpk.util.OpUtils;

public class AntiAllatori extends AntiBase {
	private final boolean callProxy;

	public AntiAllatori(Map<String, ClassNode> nodes, boolean callProxy) {
		super(nodes);
		this.callProxy = callProxy;
	}

	@Override
	public ClassNode scan(ClassNode node) {
		for (MethodNode mnode : node.methods) {
			replace(node, mnode);
		}
		return node;
	}

	private void replace(ClassNode methodHost, MethodNode method) {
		AbstractInsnNode ain = method.instructions.getFirst();
		while (ain != null && ain.getNext() != null) {
			if (ain.getType() != AbstractInsnNode.LDC_INSN || ain.getNext().getOpcode() != Opcodes.INVOKESTATIC) {
				ain = ain.getNext();
				continue;
			}
			MethodInsnNode min = (MethodInsnNode) ain.getNext();
			if (!min.desc.endsWith("(Ljava/lang/String;)Ljava/lang/String;")) {
				ain = ain.getNext();
				continue;
			}
			ClassNode owner = getNodes().get(min.owner);
			if (owner == null) {
				ain = ain.getNext();
				continue;
			}
			LdcInsnNode ldc = (LdcInsnNode) ain;
			Object o = ldc.cst;
			if (o instanceof String) {
				Object ret = callProxy ? Sandbox.getProxyIsolatedReturn(methodHost.name, method, owner, min, new Object[] { o }) : Sandbox.getIsolatedReturn(owner, min, new Object[] { o });
				if (ret != null) {
					int index = OpUtils.getIndex(ain);
					LdcInsnNode newLdc = new LdcInsnNode(ret);
					method.instructions.remove(min);
					method.instructions.set(ldc, newLdc);
					ain = method.instructions.get(index).getNext();
				} else {
					ain = ain.getNext();
				}
			}
		}
	}
}
