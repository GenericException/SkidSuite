package me.lpk.hijack;

import java.io.File;
import java.lang.management.ManagementFactory;

import com.sun.tools.attach.VirtualMachine;

public class RuntimeLoader {
	/**
	 * Loads an agent into the current VM from the given path.
	 * 
	 * @param agentPath
	 *            The path to the agent jar
	 */
	public static void loadAgentToCurrent(String agentPath) {
		String vmName = ManagementFactory.getRuntimeMXBean().getName();
		String pid = vmName.substring(0, vmName.indexOf('@'));
		loadAgent(agentPath, pid);
	}

	/**
	 * Loads an agent into a given VM.
	 * 
	 * @param agentPath
	 * @param vmID
	 */
	public static void loadAgent(String agentPath, String vmID) {
		try {
			File agentFile = new File(agentPath);
			VirtualMachine vm = VirtualMachine.attach(vmID);
			vm.loadAgent(agentFile.getAbsolutePath(), "");
			VirtualMachine.attach(vm.id());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}