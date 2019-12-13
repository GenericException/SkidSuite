package me.lpk.analysis;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class StackUtil {

	public static StackFrame[] getFrames(String owner, MethodNode mn) {
		InsnAnalyzer a = InsnAnalyzer.create(new StackHelper());
		StackFrame[] sfs = null;
		try {
			sfs = (StackFrame[]) a.analyze(owner, mn);
		} catch (AnalyzerException e) {
			//e.printStackTrace();
		}
		return sfs;
	}

}
