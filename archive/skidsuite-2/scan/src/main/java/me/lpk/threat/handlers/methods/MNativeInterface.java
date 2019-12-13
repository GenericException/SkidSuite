package me.lpk.threat.handlers.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.MethodHandler;

import me.lpk.threat.result.ThreatResult;

public class MNativeInterface extends MethodHandler {

	@Override
	public ThreatResult scanMethod(MethodNode mn) {
		// Scan method for JNI..
		List<String> methods = new ArrayList<String>();
		int opIndex = 0;
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode min = (MethodInsnNode) ain;
				// Sun's windows registry implementation and a third party's
				if (min.owner.startsWith("com/sun/jna") || min.owner.startsWith("org/xvolks/jnative")) {
					methods.add(toLocation(opIndex, mn.name, min));
				}
			}
			// TODO: Anything else?
			opIndex++;
		}
		if (methods.size() == 0) {
			return null;
		}
		return ThreatResult.withData(getName(), getDesc(), mn, methods);
	}
	
	@Override
	public String getName() {
		return "JNI/JNA Library";
	}
	@Override
	public String getDesc() {
		return  "This class can interact with non-java binaries.";
	}
}
