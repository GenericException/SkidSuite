package me.lpk.asm.threat.types;

import me.lpk.asm.threat.EnumThreatType;

public class TextThreat extends InsnThreat {
	private final String text;

	public TextThreat(EnumThreatType type, String className, String methodName, String text, int mLineNumber, int opcodeIndex) {
		super(type, className, methodName, opcodeIndex, opcodeIndex);
		this.text = text;
	}

	@Override
	public String getData() {
		return "Text: \"" + text + "\"";
	}
}
