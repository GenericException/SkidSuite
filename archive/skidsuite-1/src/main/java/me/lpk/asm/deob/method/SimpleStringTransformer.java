package me.lpk.asm.deob.method;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.asm.DescriptionUtil;
import me.lpk.gui.tabs.internal.InternalStringPatch;
import me.lpk.util.ReflectionUtil;

public class SimpleStringTransformer extends AbstractMethodTransformer {
	public final static String STRING_OUT = ")Ljava/lang/String;";
	private final String obClass, obMethod;

	public SimpleStringTransformer(ClassNode node) {
		super(node);
		this.obClass = InternalStringPatch.getObClass();
		this.obMethod = InternalStringPatch.getObMethod();
	}

	@Override
	public void transform(MethodNode method) {
		for (AbstractInsnNode ain : method.instructions.toArray()) {
			if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode) ain;
				DescriptionUtil du = DescriptionUtil.get(min.desc);
				if (min.getOpcode() == Opcodes.INVOKESTATIC && min.owner.contains(obClass) && min.name.equals(obMethod) &&
						du.getParamCount() == 1 && du.getClassType() == String.class ) {
					int opcode = min.getPrevious().getOpcode();
					Class<?> param = null;
					try{
						param = getParam(min.desc);
					}catch (Exception e){}
					if (param == null){
						continue;
					}
					if (opcode == Opcodes.LDC ) {
						LdcInsnNode ldc = (LdcInsnNode) min.getPrevious();
						method.instructions.remove(min);
						ldc.cst = ReflectionUtil.getValue(min.owner, min.name, param, ldc.cst);
					} else if (opcode == Opcodes.ICONST_0 || opcode == Opcodes.ICONST_1 || opcode == Opcodes.ICONST_2 || opcode == Opcodes.ICONST_3 || opcode == Opcodes.ICONST_4 || opcode == Opcodes.ICONST_5) {
						InsnNode in = (InsnNode) min.getPrevious();
						int inVal = opcode == Opcodes.ICONST_0 ? 0 : opcode == Opcodes.ICONST_1 ? 1 : opcode == Opcodes.ICONST_2 ? 2 : opcode == Opcodes.ICONST_3 ? 3 : opcode == Opcodes.ICONST_4 ? 4 : opcode == Opcodes.ICONST_5 ? 5 : -1;
						if (inVal >= 0) {
							method.instructions.remove(min);
							method.instructions.set(in, new LdcInsnNode(ReflectionUtil.getValue(min.owner, min.name, param, inVal)));
						}
					} else if (opcode == Opcodes.SIPUSH || opcode == Opcodes.BIPUSH) {
						IntInsnNode iin = (IntInsnNode) min.getPrevious();
						method.instructions.remove(min);
						method.instructions.set(iin, new LdcInsnNode(ReflectionUtil.getValue(min.owner, min.name, param, iin.operand)));
					}
				}
			}
		}

	}

	private Class<?> getParam(String desc) {
		if (desc.contains(")") && desc.length() > 2) {
			int end = desc.indexOf(";)");
			// If the index is -1, input is a primitive
			if (end == -1) {
				if (desc.startsWith("(I)")) {
					// Int
					return int.class;
				} else if (desc.startsWith("(J)")) {
					// Long
					return long.class;
				}
				// I doubt anyone would use double or float. So no support for
				// those needed.
				return null;
			}
			String first = desc.substring(2, end);
			if (!first.contains(";") && !first.startsWith("L")) {
				try {
					return Class.forName(first.replace("/", "."));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}