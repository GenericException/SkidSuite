package me.lpk.threat.handlers.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.MethodHandler;

import me.lpk.threat.result.ThreatResult;

public class MClassLoader extends MethodHandler {

	@Override
	public ThreatResult scanMethod(MethodNode mn) {
		// Scan method for classloaders..
		List<String> methods = new ArrayList<String>();
		int opIndex = 0;
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode min = (MethodInsnNode) ain;
				if (min.owner.contains("ClassLoader") || min.desc.contains("ClassLoader")) {
					methods.add(toLocation(opIndex, mn.name, min));
				}
			} else if (ain.getType() == AbstractInsnNode.FIELD_INSN) {
				FieldInsnNode fin = (FieldInsnNode) ain;
				if (fin.owner.contains("ClassLoader") || fin.desc.contains("ClassLoader")) {
					methods.add(toLocation(opIndex, mn.name, fin));
				}
			}
			opIndex++;
		}
		if (methods.size() == 0) {
			return null;
		}
		return ThreatResult.withData(getName(),  getDesc(), mn, methods);
	}

	@Override
	public String getName() {
		return "ClassLoader Call";
	}
	
	@Override
	public String getDesc() {
		return "This class can load new classes at runtime.";
	}
}
