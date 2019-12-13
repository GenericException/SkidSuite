package me.lpk.asm.deob;

import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.util.OpUtil;

public class DeobfuscationVisitor extends ClassVisitor {
	private final AbstractMethodTransformer trans;
	private final Map<String, ClassNode> nodes;

	public DeobfuscationVisitor(ClassVisitor cv, EnumDeobfuscation obfu, ClassNode node, Map<String, ClassNode> nodes) {
		super(OpUtil.ASM5, cv);
		this.nodes = nodes;
		trans = obfu.getMethodTransformer(this, node);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (mv == null) {
			return null;
		}
		for (MethodNode mn : trans.getNode().methods) {
			if (mn.name.equals(name) && mn.desc.equals(desc)) {
				trans.transform(mn);
			}
		}
		return mv;
	}

	public Map<String, ClassNode> getNodes() {
		return nodes;
	}
}
