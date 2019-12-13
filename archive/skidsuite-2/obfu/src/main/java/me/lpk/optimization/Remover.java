package me.lpk.optimization;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

public abstract class Remover {
	protected Set<String> keep = new HashSet<String>();

	public abstract void getUsedClasses(String mainClass, Map<String, ClassNode> nodes);

	/**
	 * 
	 * @param className
	 * @param mthdKey
	 *            <i>Example</i>:
	 * 
	 *            <pre>
	 * name(I)Z // name + desc
	 *            </pre>
	 * 
	 * @return
	 */
	public abstract boolean isMethodUsed(String className, String mthdKey);

	public Set<String> getKeptClasses() {
		return keep;
	}

}
