package me.lpk.threat.handlers;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.result.ThreatResult;

public abstract class MethodHandler implements IHandler {
	public abstract ThreatResult scanMethod(MethodNode mn);

	protected String toLocation(int opIndex, String methodNode, MethodInsnNode min) {
		return ("Opcode:Method@" + methodNode + "@" + opIndex + " - " + min.owner + "." + min.name + "." + min.desc);
	}
	
	protected String toLocation(int opIndex, String methodNode, FieldInsnNode fin) {
		return ("Opcode:Field@" + methodNode + "@" + opIndex + " - " + fin.owner + "." + fin.name + "." + fin.desc);
	}
	
	protected String toLocation(int opIndex, String methodNode, LdcInsnNode ldc) {
		return ("Opcode:Field@" + methodNode + "@" + opIndex + " - \"" + ldc.cst.toString() + "\"");
	}
}
