package me.lpk.mapping;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class MappingMode {
	/**
	 * Creates a new name for a given class
	 * 
	 * @param cn
	 * @return
	 */
	public abstract String getClassName(ClassNode cn);

	/**
	 * Creates a new name for a given method
	 * 
	 * @param mn
	 * @return
	 */
	public abstract String getMethodName(MethodNode mn);

	/**
	 * Creates a new name for a given field
	 * 
	 * @param fn
	 * @return
	 */
	public abstract String getFieldName(FieldNode fn);
}
