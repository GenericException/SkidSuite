package me.lpk.hijack;

import java.io.Serializable;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import me.lpk.hijack.match.AbstractMatcher;
import me.lpk.util.ASMUtils;

public class Refactorer implements ClassFileTransformer {
	private static final Set<AbstractMatcher<String>> matchers = new HashSet<AbstractMatcher<String>>();
	public static final Refactorer INSTANCE = new Refactorer();

	/**
	 * Registers a matcher.
	 * 
	 * @param matcher
	 * @param modder
	 */
	public static void register(AbstractMatcher<String> matcher) {
		matchers.add(matcher);
	}

	/**
	 * Receives classes before they are loaded.
	 * 
	 * @param loader
	 * @param name
	 * @param clazz
	 * @param domain
	 * @param bytes
	 * @return
	 * @throws IllegalClassFormatException
	 */
	public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
		ClassNode cn = ASMUtils.getNode(bytes);
		boolean modified = false;
		for (AbstractMatcher<String> matcher : matchers) {
			// TODO: Have a system that allows users to input a mapping
			// Name will then use the remapped class name instead
			// or...
			// Have a ClassMatcher that does that rather than doing it here
			if (matcher.isMatch(name)) {
				modified = true;
				matcher.update(loader, bytes, clazz, domain);
				matcher.modify(cn);
			}
		}
		if (modified) {
			bytes = ASMUtils.getNodeBytes(cn, true);
		}
		return bytes;
	}

	static {
		// This may look really stupid but if this isn't here the register
		// method crashes citing ClassCircularityError.
		// This is due to the improper order of loading classes. IDFK how to fix
		// it other than by loading each in order.
		// So yeah, you get this pretty static block now.
		Serializable.class.getName();
		Cloneable.class.getName();
		Iterable.class.getName();
		Collection.class.getName();
		AbstractCollection.class.getName();
		Set.class.getName();
		AbstractSet.class.getName();
		HashSet.class.getName();
	}
}