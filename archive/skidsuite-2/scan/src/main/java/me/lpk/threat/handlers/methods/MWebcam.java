package me.lpk.threat.handlers.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.MethodHandler;

import me.lpk.threat.result.ThreatResult;

public class MWebcam extends MethodHandler {

	@Override
	public ThreatResult scanMethod(MethodNode mn) {
		List<String> methods = new ArrayList<String>();
		int opIndex = 0;
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			if (ain.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode min = (MethodInsnNode) ain;
				//OpcenCV and Sarxos
				if (min.owner.contains("OpenCVFrameRecorder") || min.owner.contains("OpenCVFrameGrabber") || min.owner.contains("Webcam")) {
					methods.add(toLocation(opIndex, mn.name, min));
				}
			}else if (ain.getType() == AbstractInsnNode.LDC_INSN){
				LdcInsnNode ldc = (LdcInsnNode) ain;
				// Sarxos
				if (ldc.cst.toString().contains("Webcam device") || ldc.cst.toString().contains("Notify webcam")){
					methods.add(toLocation(opIndex, mn.name, ldc));
				}
			}
			opIndex++;
		}
		if (methods.size() == 0) {
			return null;
		}
		return  ThreatResult.withData(getName(), getDesc(), mn, methods);
	}

	@Override
	public String getName() {
		return "OpenCV/Sarxos Webcam Call";
	}
	
	@Override
	public String getDesc() {
		return "This class has methods that can access the webcam.";
	}
}
