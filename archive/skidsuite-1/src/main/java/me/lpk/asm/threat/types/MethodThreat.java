package me.lpk.asm.threat.types;

import org.objectweb.asm.tree.MethodInsnNode;

import me.lpk.asm.threat.EnumThreatType;

public class MethodThreat extends InsnThreat {
	private final String call, callOwner;

	public MethodThreat(EnumThreatType type, String className, String methodName, MethodInsnNode min, int mLineNumber, int opcodeIndex) {
		super(type, className, methodName, mLineNumber, opcodeIndex);
		call = min.name;
		callOwner = min.owner;
	}

	@Override
	public String getData() {
		return "Method called: " + callOwner + "." + call + "()";
	}

	public boolean isCallNull() {
		return call == null || call.length() == 0;
	}

}
