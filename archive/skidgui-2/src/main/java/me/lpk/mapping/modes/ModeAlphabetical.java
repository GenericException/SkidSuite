package me.lpk.mapping.modes;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.mapping.MappingMode;
import me.lpk.util.Characters;

public class ModeAlphabetical extends MappingMode {
	private int classIndex, methodIndex, fieldIndex;

	@Override
	public String getClassName(ClassNode cn) {
		return getName(classIndex++);
	}

	@Override
	public String getMethodName(MethodNode mn) {
		return getName(methodIndex++);
	}

	@Override
	public String getFieldName(FieldNode fn) {
		return getName(fieldIndex++);
	}

	/**
	 * TODO: Improve x,y,z,A... Az, Bz... so the AAA's dont stack up in larger
	 * programs
	 */
	private String getName(int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < index; i++) {
			int mod = (i + 1) % Characters.ALPHABET_BOTH.length;
			boolean even = mod == 0;
			boolean last = i == index - 1;
			if (!even && !last) {
				continue;
			}
			sb.append(Characters.ALPHABET_BOTH[mod]);
		}
		return sb.toString();
	}
}
