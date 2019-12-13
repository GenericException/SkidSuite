package me.lpk.asm.threat.types;

import me.lpk.asm.threat.EnumThreatType;

public class Threat {
	protected final String className;
	protected final EnumThreatType type;

	public Threat(EnumThreatType type, String className) {
		this.type = type;
		this.className = className;
	}

	public String getLocation() {
		return className + ".class";
	}

	public String getData() {
		return "N/A";
	}

	public String getDesc() {
		return type.getDesc();
	}

	public int getPoints() {
		return type.getPoints();
	}

	public String getClassName() {
		return className;
	}

}
