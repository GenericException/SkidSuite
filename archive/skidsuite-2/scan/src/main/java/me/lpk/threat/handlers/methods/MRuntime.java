package me.lpk.threat.handlers.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.MethodHandler;

import me.lpk.threat.result.ThreatResult;

public class MRuntime extends MethodHandler {

	@Override
	public ThreatResult scanMethod(MethodNode mn) {
		List<String> methods = new ArrayList<String>();
		int opIndex = 0;
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode min = (MethodInsnNode) ain;
				// Sun's windows registry implementation and a third party's
				if (min.owner.equals("java/lang/Runtime")) {
					methods.add(toLocation(opIndex, mn.name, min));				}
			}
			opIndex++;
		}
		if (methods.size() == 0) {
			return null;
		}
		return  ThreatResult.withData(getName(), 
				getDesc(), mn, methods);
	}

	@Override
	public String getName() {
		return "Runtime Call";
	}
	
	@Override
	public String getDesc() {
		return  "This method uses the Runtime class, which can be used for<br>" + 
				"running external programs, gathering information about<br>the executing machine,<br>" +
				"and modifying how the program closes (Such as prevention)";
	}
}
