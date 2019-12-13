package me.lpk.asm.sand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import me.lpk.asm.DescriptionUtil;
import me.lpk.util.OpUtil;
import me.lpk.util.ReflectionUtil;

public class Sandbox {
	static class BLoader extends ClassLoader {
		public BLoader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> get(String name, byte[] bytes) {
			return defineClass(name, bytes, 0, bytes.length);
		}
	}

	static class TrimVisitor extends ClassVisitor {
		MethodNode mn;

		public TrimVisitor(ClassVisitor cv, MethodNode mn) {
			super(Opcodes.ASM5, cv);
			this.mn = mn;
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			return null;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (name.equals(mn.name) && cv != null) {
				return cv.visitMethod(access, name, desc, signature, exceptions);
			}
			return null;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			if (cv != null) {
				cv.visit(version, access, "Sandbox", signature, superName, interfaces);
			}
		}
	}

	/**
	 * Jesus christ this is so ugly. But hey it means a safer reflection invoke!
	 * 
	 * @param owner
	 * @param min
	 * @param args
	 * @return
	 */
	public static Object ret(ClassNode owner, MethodInsnNode min, Object[] args) {
		ClassNode newNode = owner;
		MethodNode meth = null;
		for (MethodNode mn : owner.methods) {
			if (mn.desc.equals(min.desc) && mn.name.equals(min.name)) {
				meth = mn;
			}
		}
		ClassWriter cw = new ClassWriter(0);
		newNode.accept(new TrimVisitor(cw, meth));
		Class<?> clazz = new BLoader(ClassLoader.getSystemClassLoader()).get("Sandbox", cw.toByteArray());
		try {
			for (Method m : clazz.getMethods()) {
				m.setAccessible(true);
				return m.invoke(null, args);
			}
		} catch (RuntimeException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
