package me.lpk.mapping.modes;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.mapping.MappingMode;
import me.lpk.util.Characters;

public class ModeRandom extends MappingMode {
	private Set<String> used = new HashSet<String>();
	private int len;

	public ModeRandom(int len) {
		this.len = len;
	}

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
		while (len > sb.length() || used.contains(sb.toString())) {
			int randIndex = (int) (Math.random() * Characters.ALPHABET_BOTH.length);
			sb.append(Characters.ALPHABET_BOTH[randIndex]);
		}
		used.add(sb.toString());
		return sb.toString();
	}
}
