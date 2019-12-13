package me.lpk.mapping.modes;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.mapping.MappingMode;
import me.lpk.util.Characters;

public class ModeUnicodeEvil extends MappingMode {
	public static final int UNICODE_MAX_LENGTH = 166;
	private Set<String> used = new HashSet<String>();

	@Override
	public String getClassName(ClassNode cn) {
		return randName();
	}

	@Override
	public String getMethodName(MethodNode mn) {
		return randName();
	}

	@Override
	public String getFieldName(FieldNode fn) {
		return randName();
	}

	private String randName() {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < UNICODE_MAX_LENGTH || used.contains(sb.toString())) {
			int randIndex = (int) (Math.random() * Characters.UNICODE.length);
			sb.append(Characters.UNICODE[randIndex]);
		}
		used.add(sb.toString());
		return sb.toString();
	}
}
