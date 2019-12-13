package me.lpk.threat.handlers;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.threat.result.ThreatResult;

public abstract class ClassHandler implements IHandler{
	public abstract ThreatResult scanClass(ClassNode cn);
}
