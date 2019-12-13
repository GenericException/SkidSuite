package me.lpk.obfuscation.rename;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappedMember;
import me.lpk.util.AccessHelper;
import me.lpk.util.ParentUtils;

public class ModeSkidfuscate extends MappingModeImpl {
	/**
	 * Alphabets
	 */
	private final String c, f, m;
	private final boolean privateOnly;
	/**
	 * Current class.
	 */
	private MappedClass current;
	/**
	 * Map of descriptions with times they've been named before. Used for making
	 * as many of the same named things as possible.
	 */
	private final Map<String, Integer> descs = new HashMap<String, Integer>();
	/**
	 * Number of classes encountered
	 */
	private int classes;

	public ModeSkidfuscate(String c, String f, String m) {
		this(c, f, m, false);
	}

	public ModeSkidfuscate(String c, String f, String m, boolean p) {
		this.c = c;
		this.f = f;
		this.m = m;
		privateOnly = p;
	}

	@Override
	public String getClassName(MappedClass mc) {
		ClassNode cn = mc.getNode();
		current = mc;
		for (MethodNode mn : cn.methods) {
			if (mn.name.equals("main") && mn.desc.equals("([Ljava/lang/String;)V")) {
				return cn.name;
			}
		}
		String name = cn.name;
		if (privateOnly && cn.name.contains("/")) {
			if (!AccessHelper.isPublic(cn.access)) {
				name = cn.name.substring(0, cn.name.lastIndexOf("/") + 1) + getName(c, classes);
				classes++;
			}
		} else {
			name = getName(c, classes);
			classes++;
		}

		descs.clear();
		return name;
	}

	@Override
	public String getMethodName(MappedMember mm) {
		MethodNode mn = mm.getMethodNode();
		if (mn.desc.equals("([Ljava/lang/String;)V") && mn.name.equals("main")) {
			return "main";
		}
		if (privateOnly && !AccessHelper.isPrivate(mn.access)) {
			return mn.name;
		}
		if (!descs.containsKey(mn.desc)) {
			descs.put(mn.desc, 0);
		}
		String name = getName(m, descs.get(mn.desc));
		descs.put(mn.desc, descs.get(mn.desc) + 1);
		// Ensure that the new name will not conflict with a renamed member from a parent node
		while (ParentUtils.findMethodInParentInclusive(current, name, mm.getDesc(), false) != null){
			name = getName(m, descs.get(mn.desc));
			descs.put(mn.desc, descs.get(mn.desc) + 1);
		}
		return name;
	}

	@Override
	public String getFieldName(MappedMember mm) {
		FieldNode fn = mm.getFieldNode();
		if (privateOnly && !AccessHelper.isPrivate(fn.access)) {
			return fn.name;
		}
		if (!descs.containsKey(fn.desc)) {
			descs.put(fn.desc, 0);
		} else {
			descs.put(fn.desc, descs.get(fn.desc) + 1);
		}
		String name = getName(f, descs.get(fn.desc));
		descs.put(fn.desc, descs.get(fn.desc) + 1);
		return name;
	}

	public String getName(String alphabet, int i) {
		return getString(alphabet, i, alphabet.length());
	}

	/**
	 * Copy pasted from Integer.toString. Only change was providing the alphabet
	 * via parameter.
	 * 
	 * @param alpha
	 * @param i
	 * @param n
	 * @return
	 */
	public static String getString(String alpha, int i, int n) {
		char[] charz = alpha.toCharArray();
		if (n < 2) {
			n = 2;
		} else if (n > alpha.length()) {
			n = alpha.length();
		}
		final char[] array = new char[33];
		final boolean b = i < 0;
		int n2 = 32;
		if (!b) {
			i = -i;
		}
		while (i <= -n) {
			array[n2--] = charz[-(i % n)];
			i /= n;
		}
		array[n2] = charz[-i];
		if (b) {
			array[--n2] = '-';
		}
		return new String(array, n2, 33 - n2);
	}
}
