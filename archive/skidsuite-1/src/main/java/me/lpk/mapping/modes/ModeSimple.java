package me.lpk.mapping.modes;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.mapping.MappingMode;

public class ModeSimple extends MappingMode {
	private int classIndex, methodIndex, fieldIndex;

	@Override
	public String getClassName(ClassNode cn) {
		return "Class" + classIndex++;
	}

	@Override
	public String getMethodName(MethodNode mn) {
		return "method" + methodIndex++;
	}

	@Override
	public String getFieldName(FieldNode fn) {
		return "field" + fieldIndex++;
	}
}
