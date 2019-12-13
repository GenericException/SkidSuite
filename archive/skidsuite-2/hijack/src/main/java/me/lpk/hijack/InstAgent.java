package me.lpk.hijack;

import java.lang.instrument.Instrumentation;

import me.lpk.log.Logger;

public class InstAgent {
	private static Instrumentation instrumentation;

	/**
	 * Called when declared in JVM arguments.
	 * 
	 * @param args
	 * @param inst
	 * @throws Exception
	 */
	public static void premain(String args, Instrumentation inst) throws Exception {
		setAndAddTransformer(inst);
	}

	/**
	 * Called when declared at runtime.
	 * 
	 * @param args
	 * @param inst
	 * @throws Exception
	 */
	public static void agentmain(String args, Instrumentation inst) throws Exception {
		setAndAddTransformer(inst);
	}

	private static void setAndAddTransformer(Instrumentation inst) {
		instrumentation = inst;
		instrumentation.addTransformer(Refactorer.INSTANCE);
		Logger.logLow("Instrumentation: " + "[ Redefinition:" + instrumentation.isRedefineClassesSupported() + ", Retransformation:"
				+ instrumentation.isRetransformClassesSupported() + " ]");


		// Registering class modders for Refactorer goes here
	}

	public static void initialize(String jarFilePath) {
		if (instrumentation == null) {
			RuntimeLoader.loadAgentToCurrent(jarFilePath);
		}
	}

	public static Instrumentation getInst() {
		return instrumentation;
	}
}