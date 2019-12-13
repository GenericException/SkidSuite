package me.lpk.hijack.modder;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import org.objectweb.asm.Opcodes;

import me.lpk.hijack.match.AbstractMatcher;
import me.lpk.util.AccessHelper;

/**
 * A ClassModder that makes everything public at runtime than can be made
 * public.
 */
public class AccessModder extends ClassModder {

	public AccessModder(AbstractMatcher<?> matcher) {
		super(matcher);
	}
	
	@Override
	public void modify(ClassNode cn) {
		cn.access = acc(cn.access);
		for (FieldNode field : cn.fields) {
			field.access = acc(field.access);
		}
		for (MethodNode method : cn.methods) {
			if (method.name.contains("<")) {
				continue;
			}
			method.access = acc(method.access);
		}
	}

	private int acc(int cnaccess) {
		int access = Opcodes.ACC_PUBLIC;
		if (AccessHelper.isAbstract(cnaccess)) {
			access |= Opcodes.ACC_ABSTRACT;
		}
		if (AccessHelper.isStatic(cnaccess)) {
			access |= Opcodes.ACC_STATIC;
		}
		if (AccessHelper.isEnum(cnaccess)) {
			access |= Opcodes.ACC_ENUM;
		}
		if (AccessHelper.isAnnotation(cnaccess)) {
			access |= Opcodes.ACC_ANNOTATION;
		}
		if (AccessHelper.isInterface(cnaccess)) {
			access |= Opcodes.ACC_INTERFACE;
		}
		if (AccessHelper.isVolatile(cnaccess)) {
			access |= Opcodes.ACC_VOLATILE;
		}
		if (AccessHelper.isVolatile(cnaccess)) {
			access |= Opcodes.ACC_TRANSIENT;
		}
		if (AccessHelper.isSynchronized(cnaccess)) {
			access |= Opcodes.ACC_SYNCHRONIZED;
		}
		if (AccessHelper.isNative(cnaccess)) {
			access |= Opcodes.ACC_NATIVE;
		}
		return access;
	}

	
	/*
	@Override
	public void modify(ClassNode cn) {
		for (FieldNode field : cn.fields) {
			if (field.access == Opcodes.ACC_PRIVATE || field.access == Opcodes.ACC_PROTECTED) {
				field.access = Opcodes.ACC_PUBLIC;
			}
		}
		for (MethodNode method : cn.methods) {
			if (method.name.contains("<")) {
				continue;
			}
			if (method.access == Opcodes.ACC_PRIVATE || method.access == Opcodes.ACC_PROTECTED) {
				method.access = Opcodes.ACC_PUBLIC;
			}
		}
	}
	*/
}
