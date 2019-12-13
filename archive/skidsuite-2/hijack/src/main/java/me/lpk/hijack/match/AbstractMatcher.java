package me.lpk.hijack.match;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.hijack.modder.ClassModder;

public abstract class AbstractMatcher<T> {
	/**
	 * A list of ClassModder that will be modified if the match is successful.
	 */
	private final List<ClassModder> modders = new ArrayList<ClassModder>();
	private ClassLoader currentLoader;
	private byte[] currentBytes;
	private Class<?> currentClass;
	private ProtectionDomain currentDomain;

	/**
	 * Updates the matcher with the ClassLoader, bytes, Class, and
	 * ProtectionDomain belonging to the current class being modified.
	 * 
	 * @param loader
	 * @param bytes
	 * @param clazz
	 * @param domain
	 */
	public void update(ClassLoader loader, byte[] bytes, Class<?> clazz, ProtectionDomain domain) {
		this.currentLoader = loader;
		this.currentBytes = bytes;
		this.currentClass = clazz;
		this.currentDomain = domain;
	}

	/**
	 * Sends a ClassNode to all receiving ClassModders.
	 * 
	 * @param cn
	 */
	public final void modify(ClassNode cn) {
		for (ClassModder modder : modders) {
			modder.modify(cn);
		}
	}

	/**
	 * Returns if the generic argument matches a user-defined pattern.
	 * 
	 * @param t
	 * @return
	 */
	public abstract boolean isMatch(T t);

	/**
	 * Adds a ClassModder to the matcher.
	 * 
	 * @param modder
	 */
	public void addReceiver(ClassModder modder) {
		this.modders.add(modder);
	}

	/**
	 * Adds an array of ClassModders to the matcher.
	 * 
	 * @param modders
	 */
	public void addReceivers(ClassModder... modders) {
		this.modders.addAll(Arrays.asList(modders));
	}

	/**
	 * Adds a list of ClassModders to the matcher.
	 * 
	 * @param modders
	 */
	public void addReceivers(Collection<ClassModder> modders) {
		this.modders.addAll(modders);
	}

	/**
	 * Returns the list of ClassModders belonging to the matcher.
	 * 
	 * @return
	 */
	public final List<ClassModder> getModders() {
		return modders;
	}

	/**
	 * Returns the ClassLoader for the ClassNode currently being modified.
	 * 
	 * @return
	 */
	public ClassLoader getCurrentLoader() {
		return currentLoader;
	}

	/**
	 * Returns the bytes of the ClassNode currently being modified.
	 * 
	 * @return
	 */
	public byte[] getCurrentBytes() {
		return currentBytes;
	}

	/**
	 * Returns the Class belonging to the ClassNode currently being modified.
	 * 
	 * @return
	 */
	public Class<?> getCurrentClass() {
		return currentClass;
	}

	/**
	 * Returns the ProtectionDomain of the ClassNode currently being modified.
	 * 
	 * @return
	 */
	public ProtectionDomain getCurrentDomain() {
		return currentDomain;
	}
}
