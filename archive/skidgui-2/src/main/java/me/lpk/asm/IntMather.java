package me.lpk.asm;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import me.lpk.gui.tabs.internal.handle.FieldHandler;
import me.lpk.gui.tabs.internal.handle.FrameHandler;
import me.lpk.gui.tabs.internal.handle.IincHandler;
import me.lpk.gui.tabs.internal.handle.InsnHandler;
import me.lpk.gui.tabs.internal.handle.IntHandler;
import me.lpk.gui.tabs.internal.handle.InvokeDynHandler;
import me.lpk.gui.tabs.internal.handle.JumpHandler;
import me.lpk.gui.tabs.internal.handle.LabelHandler;
import me.lpk.gui.tabs.internal.handle.LdcHandler;
import me.lpk.gui.tabs.internal.handle.LineHandler;
import me.lpk.gui.tabs.internal.handle.LookupHandler;
import me.lpk.gui.tabs.internal.handle.MethodHandler;
import me.lpk.gui.tabs.internal.handle.MultiArrayHandler;
import me.lpk.gui.tabs.internal.handle.NodeHandler;
import me.lpk.gui.tabs.internal.handle.SwitchHandler;
import me.lpk.gui.tabs.internal.handle.TypeHandler;
import me.lpk.gui.tabs.internal.handle.VarHandler;
import me.lpk.util.OpUtil;
import me.lpk.util.ReflectionUtil;

public class IntMather {
	private final Stack<AbstractInsnNode> instructions = new Stack<AbstractInsnNode>();

	public void push(AbstractInsnNode ain) {
		instructions.push(ain);
	}

	private final Map<Integer, NodeHandler> handlers = new HashMap<Integer, NodeHandler>(getHandlers());

	private Map<? extends Integer, ? extends NodeHandler> getHandlers() {
		HashMap<Integer, NodeHandler> handlers = new HashMap<Integer, NodeHandler>();
		handlers.put(AbstractInsnNode.FIELD_INSN, new FieldHandler());
		handlers.put(AbstractInsnNode.FRAME, new FrameHandler());
		handlers.put(AbstractInsnNode.IINC_INSN, new IincHandler());
		handlers.put(AbstractInsnNode.INT_INSN, new IntHandler());
		handlers.put(AbstractInsnNode.INVOKE_DYNAMIC_INSN, new InvokeDynHandler());
		handlers.put(AbstractInsnNode.JUMP_INSN, new JumpHandler());
		handlers.put(AbstractInsnNode.LABEL, new LabelHandler());
		handlers.put(AbstractInsnNode.LDC_INSN, new LdcHandler());
		handlers.put(AbstractInsnNode.LINE, new LineHandler());
		handlers.put(AbstractInsnNode.LOOKUPSWITCH_INSN, new LookupHandler());
		handlers.put(AbstractInsnNode.METHOD_INSN, new MethodHandler());
		handlers.put(AbstractInsnNode.MULTIANEWARRAY_INSN, new MultiArrayHandler());
		handlers.put(AbstractInsnNode.TABLESWITCH_INSN, new SwitchHandler());
		handlers.put(AbstractInsnNode.TYPE_INSN, new TypeHandler());
		handlers.put(AbstractInsnNode.VAR_INSN, new VarHandler());
		handlers.put(AbstractInsnNode.INSN, new InsnHandler());
		return handlers;
	}

	public Stack<Integer> calculate() {
		Stack<Integer> stack = new Stack<Integer>();
		while (instructions.size() > 0) {
			AbstractInsnNode ain = instructions.pop();
			System.out.print("\t\t" + handlers.get(ain.getType()).asText(ain));
			switch (ain.getOpcode()) {
			case Opcodes.F_NEW:
			case Opcodes.NEW:
				stack.push(-321);
				break;
			case Opcodes.ICONST_M1:
			case Opcodes.ICONST_0:
			case Opcodes.ICONST_1:
			case Opcodes.ICONST_2:
			case Opcodes.ICONST_3:
			case Opcodes.ICONST_4:
			case Opcodes.ICONST_5:
			case Opcodes.SIPUSH:
			case Opcodes.BIPUSH:
				stack.push(OpUtil.getIntValue(ain));
				break;
			case Opcodes.IMUL:
				int multiply = stack.pop() * stack.pop();
				stack.push(multiply);
				break;
			case Opcodes.POP:
				stack.pop();
				break;
			case Opcodes.POP2:
				stack.pop();
				stack.pop();
				break;
			case Opcodes.SWAP:
				int swap1 = stack.pop();
				int swap2 = stack.pop();
				stack.push(swap1);
				stack.push(swap2);
				break;
			case Opcodes.IAND:
				int and1 = stack.pop();
				int and2 = stack.pop();
				int andBit = and2 & and1;
				stack.push(andBit);
				break;
			case Opcodes.ISHL:
				int shl1 = stack.pop();
				int shl2 = stack.pop();
				int shiftLeft = shl2 << shl1;
				stack.push(shiftLeft);
				break;
			case Opcodes.IUSHR:
				int ushl1 = stack.pop();
				int ushl2 = stack.pop();
				int ushiftLeft = ushl2 >>> ushl1;
				stack.push(ushiftLeft);
				break;
			case Opcodes.ISHR:
				int shr1 = stack.pop();
				int shr2 = stack.pop();
				int shiftRight = shr2 >> shr1;
				stack.push(shiftRight);
				break;
			case Opcodes.IREM:
				int rem1 = stack.pop();
				int rem2 = stack.pop();
				if (rem1 == 0) {
					System.out.println("\tMather tried to divide by zero<IREM>! Pushing 0 as temporary replacement!");
					stack.push(0);
				} else {
					int remainder = rem2 % rem1;
					stack.push(remainder);
				}
				break;
			case Opcodes.IOR:
				int ior1 = stack.pop();
				int ior2 = stack.pop();
				int iorBit = ior2 | ior1;
				stack.push(iorBit);
				break;
			case Opcodes.IXOR:
				int ixor1 = stack.pop();
				int ixor2 = stack.pop();
				int ixorBit = ixor2 ^ ixor1;
				stack.push(ixorBit);
				break;
			case Opcodes.ISUB:
				int sub1 = stack.pop();
				int sub2 = stack.pop();
				int subtract = sub2 - sub1;
				stack.push(subtract);
				break;
			case Opcodes.IADD:
				int add1 = stack.pop();
				int add2 = stack.pop();
				int addition = add2 + add1;
				stack.push(addition);
				break;
			case Opcodes.IDIV:
				int div1 = stack.pop();
				int div2 = stack.pop();
				if (div2 == 0) {
					System.out.println("\tMather tried to divide by zero<IDIV>! Pushing 0 as temporary replacement!");
					stack.push(0);
				} else {
					int division = div2 / div1;
					stack.push(division);
				}
				break;
			case Opcodes.DUP:
				// value --> value, value
				stack.push(stack.peek());
				break;
			case Opcodes.DUP_X1:
				// value2, value1 --> value1, value2, value1
				int in1 = stack.pop();
				int in2 = stack.pop();
				stack.push(in1);
				stack.push(in2);
				stack.push(in1);
				break;
			case Opcodes.DUP_X2:
				// value3, value2, value1 --> value1, value3, value2, value1
				int int1 = stack.pop();
				int int2 = stack.pop();
				int int3 = stack.pop();
				stack.push(int1);
				stack.push(int3);
				stack.push(int2);
				stack.push(int1);
				break;
			case Opcodes.DUP2:
				// {value2, value1} --> {value2, value1}, {value2, value1}
				int i1 = stack.pop();
				int i2 = stack.pop();
				stack.push(i2);
				stack.push(i1);
				stack.push(i2);
				stack.push(i1);
				break;
			case Opcodes.DUP2_X1:
				// value3, {value2, value1} -->
				// {value2, value1}, value3, {value2, value1}
				int id1 = stack.pop();
				int id2 = stack.pop();
				int id3 = stack.pop();
				stack.push(id2);
				stack.push(id1);
				stack.push(id3);
				stack.push(id2);
				stack.push(id1);
				break;
			case Opcodes.DUP2_X2:
				// {value4, value3}, {value2, value1} -->
				// {value2, value1}, {value4, value3}, {value2, value1}
				int ind1 = stack.pop();
				int ind2 = stack.pop();
				int ind3 = stack.pop();
				int ind4 = stack.pop();
				stack.push(ind2);
				stack.push(ind1);
				stack.push(ind4);
				stack.push(ind3);
				stack.push(ind2);
				stack.push(ind1);
				break;
			case Opcodes.LDC:
				LdcInsnNode ldc = (LdcInsnNode) ain;
				if (ldc.cst instanceof Number) {
					stack.push(((Number) ldc.cst).intValue());
				}else{
					stack.push(-321);
				}
				break;
			case Opcodes.INVOKESTATIC:
			case Opcodes.INVOKEINTERFACE:
			case Opcodes.INVOKEDYNAMIC:
			case Opcodes.INVOKEVIRTUAL:
				MethodInsnNode min = (MethodInsnNode) ain;
				if (!min.desc.endsWith("I")) {
					break;
				}
				if (!min.desc.equals("()I")) {
					System.out.println("\tMather encountered a method that may need invoking, but ignored it! " + min.desc);
					break;
				}
				Object retVal = ReflectionUtil.getValue(min.owner, min.name, int.class, null);
				if (retVal == null) {
					System.out.println("\tMather encountered a method that may need invoking, but failed! " + min.desc);
				} else if (retVal instanceof Number) {
					stack.push(((Number) retVal).intValue());
				}
				break;
			}
		}

		return stack;

	}
}
