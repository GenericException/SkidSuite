package me.lpk.asm.threat.types;

import me.lpk.asm.threat.EnumThreatType;

public class InsnThreat extends Threat {
	protected final String methodName;
	protected final int mLineNumber, opcodeIndex;

	public InsnThreat(EnumThreatType type, String className, String methodName, int mLineNumber, int opcodeIndex) {
		super(type, className);
		this.methodName = methodName;
		this.mLineNumber = mLineNumber;
		this.opcodeIndex = opcodeIndex;
	}

	@Override
	public String getLocation() {
		return isMethodNull() ? className + ".class" : (className + "." + methodName + "()" + " - Line" + (mLineNumber >= 0 ? mLineNumber : "(Bytecode) " + opcodeIndex));
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isMethodNull() {
		return methodName == null || methodName.length() == 0;
	}

	public int getLineNumber() {
		return mLineNumber;
	}

	public int getOpcodeIndex() {
		return opcodeIndex;
	}
}
