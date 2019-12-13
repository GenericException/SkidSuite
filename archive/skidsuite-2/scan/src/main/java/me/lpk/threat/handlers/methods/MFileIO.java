package me.lpk.threat.handlers.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.MethodHandler;

import me.lpk.threat.result.ThreatResult;

public class MFileIO extends MethodHandler {

	@Override
	public ThreatResult scanMethod(MethodNode mn) {
		List<String> methods = new ArrayList<String>();
		int opIndex = 0;
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode min = (MethodInsnNode) ain;
				if (min.owner.contains("File")
						&& (min.name.contains("delete") || min.name.contains("mkdir") || min.name.contains("createNew") || min.name.contains("createTemp"))) {
					methods.add(toLocation(opIndex, mn.name, min));
				}
			}
			opIndex++;
		}
		if (methods.size() == 0) {
			return null;
		}
		return ThreatResult.withData(getName(),getDesc(), mn, methods);
	}

	@Override
	public String getName() {
		return "FileIO";
	}
	@Override
	public String getDesc() {
		return  "This class has methods that interact with the file system.";
	}
}
