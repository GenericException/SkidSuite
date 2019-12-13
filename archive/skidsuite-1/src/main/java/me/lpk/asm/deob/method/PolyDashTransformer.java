package me.lpk.asm.deob.method;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import me.lpk.asm.deob.DeobfuscationVisitor;

public class PolyDashTransformer extends DeobTransformer {

	public PolyDashTransformer(DeobfuscationVisitor dv, ClassNode node) {
		super(dv, node);
	}

	@Override
	public void transform(MethodNode method) {
		// Supposed to work on DashO samples with multiple kinds of DashO calls.
		// Uses a stack emulator to emulate the numeric parameters.
		// TODO: Finsh Sandbox.test();

		/*
		 * for (AbstractInsnNode ain : method.instructions.toArray()) { if
		 * (ain.getType() == AbstractInsnNode.METHOD_INSN) { MethodInsnNode min
		 * = (MethodInsnNode) ain; String sub =
		 * min.desc.substring(min.desc.indexOf("("), min.desc.indexOf(")"));
		 * boolean dashOMethod = min.desc.endsWith("Ljava/lang/String;") &&
		 * sub.contains("I") && sub.contains("Ljava/lang/String;"); if
		 * (!dashOMethod) { continue; } LdcInsnNode ldc = null; boolean
		 * stopMather = false; List<AbstractInsnNode> instructs = new
		 * ArrayList<AbstractInsnNode>(); AbstractInsnNode search =
		 * min.getPrevious(); IntMather mather = new IntMather(); while
		 * ((!stopMather) && search.getPrevious() != null) { int op =
		 * search.getOpcode(); if (!stopMather) { mather.push(search);
		 * instructs.add(search); } int type = search.getType(); if (type ==
		 * AbstractInsnNode.LDC_INSN) { ldc = (LdcInsnNode) search; } else if
		 * (type == AbstractInsnNode.JUMP_INSN) { stopMather = true; } else if
		 * (op == Opcodes.ATHROW) { stopMather = true; } else if (op ==
		 * Opcodes.NEW) { TypeInsnNode tin = (TypeInsnNode) search; if
		 * (!tin.desc.equals("I")) { System.out.println(tin.desc); stopMather =
		 * true; } }
		 * 
		 * search = search.getPrevious(); } if (ldc == null) {
		 * System.out.println( "Couldn't find ldc"); return; }
		 * System.out.println("\t\tMather {"); Stack<Integer> stack =
		 * mather.calculate(); System.out.println("\t\t}");
		 * 
		 * if (stack.size() == 0) { System.out.println( "Mather failed.");
		 * return; } boolean stringFirst = false; DescData descDat =
		 * DescData.get(min.desc); Map<Integer, String> d = descDat.getParams();
		 * for (int i = 0; i < d.size(); i++) { String s = d.get(i); if (s !=
		 * null && s.contains("String") && i == 0) { stringFirst = true; break;
		 * } } List<Object> args = new ArrayList<Object>(); if (stringFirst) {
		 * args.add(ldc.cst); while (stack.size() > 0) { int i = stack.pop(); if
		 * (i != -321) { args.add(1, i); } } } else { while (stack.size() > 0) {
		 * int i = stack.pop(); if (i != -321) { args.add(0, i); } }
		 * args.add(ldc.cst); } ClassNode owner = dv.getNodes().get(min.owner);
		 * MethodNode fuq = null; for (MethodNode mn : owner.methods) { if
		 * (mn.name.equals(min.name) && min.desc.equals(mn.desc)) { fuq = mn;
		 * break; } } if (fuq == null) { System.out.println(
		 * "Couldn't get method: " + min.owner + "." + min.name + min.desc);
		 * return; } System.out.println( "The method that will be invoked: " +
		 * min.desc); for (Object o : args) { System.out.println("\tArg: " + o);
		 * if (o == null) { System.out.println(
		 * "An argument failed. Skipping..."); return; } } Object o =
		 * Sandbox.ret(owner, min, args.toArray()); if (o != null) { for (int k
		 * = 0; k < descDat.getParamCount(); k++) {
		 * method.instructions.insertBefore(min, new InsnNode(Opcodes.POP)); }
		 * method.instructions.set(min, new LdcInsnNode(o.toString())); } } }
		 */
	}

}