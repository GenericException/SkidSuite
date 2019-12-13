package me.lpk.hijack.modder;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.hijack.match.AbstractMatcher;

public abstract class ClassModder {
	private final AbstractMatcher<?> matcher;

	public ClassModder(AbstractMatcher<?> matcher) {
		this.matcher = matcher;
	}

	public abstract void modify(ClassNode cn);

	/**
	 * Returns the matcher that modder belongs to.
	 * 
	 * @return
	 */
	public AbstractMatcher<?> getMatcher() {
		return matcher;
	}
}
