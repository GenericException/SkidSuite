package me.lpk.asm.threat;

import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.asm.threat.types.Threat;
import me.lpk.util.AccessHelper;

public class ThreatClassVisitor extends ClassVisitor {
	private static final boolean skip$vals = true;
	private final Map<String, ClassNode> nodes;
	private final AbstractMethodTransformer trans;
	private final ClassNode cn;
	private final ClassThreat ct;

	public ThreatClassVisitor(ClassVisitor cv, ClassNode cn, ClassThreat ct, Map<String, ClassNode> nodes) {
		super(Opcodes.ASM5, cv);
		trans = new ThreatMethodTransformer(cn, ct, this);
		this.nodes = nodes;
		this.cn = cn;
		this.ct = ct;

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

	/**
	 * Used to analyze class structure.
	 */
	@Override
	public void visitEnd() {
		int synthFields = 0, synthMethods = 0, totalFields = 0, totalMethods = 0;
		boolean regHKLU = false, regHKLM = false, regReadAll = false, sunRegistry = false, regKeyStrFound = false;
		// Scan methods for unnatural synthetic tag occurrence and registry
		// constants.
		for (FieldNode fn : cn.fields) {
			if (fn == null) {
				continue;
			}
			totalFields++;
			if (AccessHelper.isSynthetic(fn.access) && (skip$vals ? !fn.name.contains("$") : true)) {
				synthFields++;
			}
			if (fn.desc.equals("I")) {
				if (fn.value == null) {
					continue;
				}
				if (fn.value.equals(0x80000001)) {
					regHKLU = true;
				} else if (fn.value.equals(0x80000002)) {
					regHKLM = true;
				} else if (fn.value.equals(0xf003f)) {
					regReadAll = true;
				}
			}
		}
		// Scan methods for unnatural synthetic tag occurrence and registry
		// method calls.
		for (MethodNode mn : cn.methods) {
			totalMethods++;
			if (AccessHelper.isSynthetic(mn.access) && (skip$vals ? !mn.name.contains("$") : true)) {
				// Discount <init> and <clinit> from calculations. Shouldn't be
				// sythetic anyways...
				if (mn.name.contains("<")) {
					continue;
				}
				synthMethods++;
			}
			for (AbstractInsnNode ain : mn.instructions.toArray()) {
				if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
					MethodInsnNode min = (MethodInsnNode) ain;
					// Sun's windows registry implementation
					if (min.owner.startsWith("com/sun/jna") && (min.owner.contains("Advapi32Util") || min.owner.contains("WinReg"))) {
						sunRegistry = true;
					}
				}
			}
		}
		double synthFieldPercent = totalFields == 0 ? 0 : synthFields / totalFields;
		double synthMethodPercent = totalMethods == 0 ? 0 : synthMethods / totalMethods;
		if ((synthFieldPercent + synthMethodPercent) / 2 > 0.65) {
			Threat t = new Threat(EnumThreatType.Class_NonNormal_Synthetic, cn.name);
			ct.addThreat(EnumThreatType.Class_NonNormal_Synthetic, t);
		}
		if ((regHKLU && regHKLM && regReadAll) || (regKeyStrFound || sunRegistry)) {
			Threat t = new Threat(EnumThreatType.Class_RegEdit, cn.name);
			ct.addThreat(EnumThreatType.Class_RegEdit, t);
		}
		if (isClass(cn.name, "java/lang/ClassLoader")) {
			Threat t = new Threat(EnumThreatType.Class_Classloader, cn.name);
			ct.addThreat(EnumThreatType.Class_Classloader, t);
		}
	}

	/**
	 * Checks if testClass is a child of checkClass.
	 * 
	 * @param testClass
	 * @param checkClass
	 * @return
	 */
	public boolean isClass(String testClass, String checkClass) {
		if (nodes.containsKey(testClass)) {
			ClassNode cn = nodes.get(testClass);
			boolean escape = false;
			while (!escape) {
				// TODO: Check for interfaces that extend interfaces.
				if (cn.name.equals(checkClass) || cn.superName.equals(checkClass) || cn.interfaces.contains(checkClass)) {
					return true;
				} else if (cn.parent != null) {
					cn = cn.parent;
				} else {
					escape = true;
				}
			}
		}
		return false;
	}

	public Map<String, ClassNode> getNodes() {
		return nodes;
	}
}
