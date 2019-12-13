package me.lpk.threat.handlers.classes;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.ClassHandler;

import me.lpk.threat.result.ThreatResult;

public class CWinRegHandler extends ClassHandler {

	@Override
	public ThreatResult scanClass(ClassNode cn) {
		boolean regHKLU = false, regHKLM = false, regReadAll = false, sunRegistry = false, regKeyStrFound = false;
		// Scan fields for registry constants.
		for (FieldNode fn : cn.fields) {
			if (fn == null) {
				continue;
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
		// Scan methods registry method calls.
		for (MethodNode mn : cn.methods) {
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
		if ((regHKLU && regHKLM && regReadAll) || (regKeyStrFound || sunRegistry)) {
			return new ThreatResult(getName(), getDesc(), cn.name);
		}
		return null;
	}
	@Override
	public String getName() {
		return "Windows RegEdit";
	}
	
	@Override
	public String getDesc() {
		return "This class can modify the window's registry.";
	}
}
