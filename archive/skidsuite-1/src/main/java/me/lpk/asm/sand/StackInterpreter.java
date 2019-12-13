package me.lpk.asm.sand;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.lpk.asm.DescriptionUtil;
import me.lpk.util.OpUtil;
import me.lpk.util.PrimitiveUtils;
import me.lpk.util.ReflectionUtil;

public class StackInterpreter {
	public static Stack<Object> test(InsnList instructions, int localsSize, Object... args) {
		Stack<Object> stack = new Stack<Object>();
		Object[] locals = new Object[localsSize];
		int argIndex = 0;
		for (Object arg : args) {
			locals[argIndex] = arg;
			argIndex++;
		}
		boolean done = false;
		AbstractInsnNode ain = instructions.get(0);
		while (!done) {
			System.out.println("\t\t\t\t" + OpUtil.opcodes.get(ain.getOpcode()));
			switch (ain.getOpcode()) {
			// If logic
			case Opcodes.IF_ACMPEQ:
			case Opcodes.IF_ICMPEQ:
				Object comp1 = stack.pop();
				Object comp2 = stack.pop();
				if (comp2.equals(comp1)) {
					JumpInsnNode jin = (JumpInsnNode) ain;
					ain = jin.label;
				}
				break;
			case Opcodes.IF_ACMPNE:
			case Opcodes.IF_ICMPNE:
				Object compa1 = stack.pop();
				Object compa2 = stack.pop();
				if (!compa2.equals(compa1)) {
					JumpInsnNode jin = (JumpInsnNode) ain;
					ain = jin.label;
				}
				break;
			case Opcodes.IF_ICMPGE:
				int icomp1 = (int) stack.pop();
				int icomp2 = (int) stack.pop();
				if (icomp1 >= icomp2) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IF_ICMPGT:
				int icompa1 = (int) stack.pop();
				int icompa2 = (int) stack.pop();
				if (icompa1 > icompa2) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IF_ICMPLE:
				int icompar1 = (int) stack.pop();
				int icompar2 = (int) stack.pop();
				if (icompar1 <= icompar2) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IF_ICMPLT:
				int icompare1 = (int) stack.pop();
				int icompare2 = (int) stack.pop();
				if (icompare1 < icompare2) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFEQ:
				int ifeq = (int) stack.pop();
				if (ifeq == 0) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFGE:
				int ifge = (int) stack.pop();
				if (ifge >= 0) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFGT:
				int ifgt = (int) stack.pop();
				if (ifgt > 0) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFLE:
				int ifle = (int) stack.pop();
				if (ifle <= 0) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFLT:
				int iflt = (int) stack.pop();
				if (iflt < 0) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFNE:
				int ifne = (int) stack.pop();
				if (ifne != 0) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFNONNULL:
				if (stack.pop() != null) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.IFNULL:
				if (stack.pop() == null) {
					ain = ((JumpInsnNode) ain).label;
				}
				break;
			case Opcodes.FCMPG:
			case Opcodes.FCMPL:
				float floadComp1 = (float) stack.pop();
				float floadComp2 = (float) stack.pop();
				stack.push(floadComp1 == floadComp2 ? 1 : 0);
				break;
			case Opcodes.DCMPG:
			case Opcodes.DCMPL:
				double doubleComp1 = (double) stack.pop();
				double doubleComp2 = (double) stack.pop();
				stack.push(doubleComp1 == doubleComp2 ? 1 : 0);
				break;
			// Non-if jumps
			case Opcodes.JSR:
				JumpInsnNode jsr = (JumpInsnNode) ain;
				stack.push(jsr);
				ain = jsr.label;
				break;
			case Opcodes.GOTO:
				ain = ((JumpInsnNode) ain).label;
				break;
			case Opcodes.LOOKUPSWITCH:
				int lsinIndex = (int) stack.pop();
				LookupSwitchInsnNode lsin = (LookupSwitchInsnNode) ain;
				if (lsinIndex < lsin.labels.size() && lsinIndex >= 0) {
					ain = lsin.labels.get(lsinIndex);
				} else {
					ain = lsin.dflt;
				}
				break;
			case Opcodes.TABLESWITCH:
				int tsinIndex = (int) stack.pop();
				TableSwitchInsnNode tsin = (TableSwitchInsnNode) ain;
				if (tsinIndex < tsin.labels.size() && tsinIndex >= 0) {
					ain = tsin.labels.get(tsinIndex);
				} else {
					ain = tsin.dflt;
				}
				break;
			// Returns
			case Opcodes.LRETURN:
			case Opcodes.FRETURN:
			case Opcodes.DRETURN:
			case Opcodes.IRETURN:
			case Opcodes.ARETURN:
				System.out.println("\tStackInter returned: " + stack.peek());
				return stack;
			case Opcodes.RET:
				System.out.println("\tStackInter encountered a RET opcode! Stack peek:" + stack.peek());
				VarInsnNode retVin = (VarInsnNode) ain;
				Stack<Object> retItem = new Stack<Object>();
				retItem.push(locals[retVin.var]);
				return retItem;
			// Instantiation
			case Opcodes.NEW:
				TypeInsnNode cast = (TypeInsnNode) ain;
				System.out.println("\tStackInter encountered NEW!");
				try {
					stack.push(Type.getType(cast.desc).getClass().newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			case Opcodes.NEWARRAY:
				int len = (int) stack.pop();
				IntInsnNode cast2 = (IntInsnNode) ain;
				Object arrType = null;
				switch (cast2.operand) {
				case Opcodes.T_BOOLEAN:
					arrType = new boolean[len];
					break;
				case Opcodes.T_CHAR:
					arrType = new char[len];
					break;
				case Opcodes.T_FLOAT:
					arrType = new float[len];
					break;
				case Opcodes.T_DOUBLE:
					arrType = new double[len];
					break;
				case Opcodes.T_BYTE:
					arrType = new byte[len];
					break;
				case Opcodes.T_SHORT:
					arrType = new short[len];
					break;
				case Opcodes.T_INT:
					arrType = new int[len];
					break;
				case Opcodes.T_LONG:
					arrType = new long[len];
					break;
				default:
					throw new IllegalArgumentException("Unknown newarray type " + cast2.operand);
				}
				stack.push(arrType);
				break;
			case Opcodes.ANEWARRAY:
				stack.push(new Object[(int) stack.pop()]);
				break;
			case Opcodes.CHECKCAST:
				System.out.println("\tStackInter tried to CHECKCAST!");
				break;
			case Opcodes.INSTANCEOF: {
				System.out.println("\tStackInter tried to INSTANCEOF!");
				TypeInsnNode castInst = (TypeInsnNode) ain;
				Object obj = stack.pop();
				Type typeCast = Type.getType(castInst.desc);
				Type typeObj = Type.getType(obj.getClass());
				stack.push(typeObj.equals(typeCast) ? 1 : 0);
				break;
			}
			case Opcodes.ARRAYLENGTH:
				stack.push(Array.getLength(stack.pop()));
				break;
			// Local variables
			case Opcodes.ISTORE:
			case Opcodes.LSTORE:
			case Opcodes.FSTORE:
			case Opcodes.DSTORE:
			case Opcodes.ASTORE:
				VarInsnNode vin = (VarInsnNode) ain;
				locals[vin.var] = stack.pop();
				break;
			case Opcodes.ILOAD:
			case Opcodes.LLOAD:
			case Opcodes.FLOAD:
			case Opcodes.DLOAD:
			case Opcodes.ALOAD:
				VarInsnNode vin2 = (VarInsnNode) ain;
				stack.push(locals[vin2.var]);
				break;
			case Opcodes.IALOAD:
			case Opcodes.LALOAD:
			case Opcodes.FALOAD:
			case Opcodes.DALOAD:
			case Opcodes.BALOAD:
			case Opcodes.CALOAD:
			case Opcodes.SALOAD:
			case Opcodes.AALOAD:
				int aaIndex = (int) stack.pop();
				Object arrayObject = stack.pop();
				stack.push(Array.get(arrayObject, aaIndex));
				break;
			case Opcodes.IASTORE:
			case Opcodes.LASTORE:
			case Opcodes.FASTORE:
			case Opcodes.DASTORE:
			case Opcodes.BASTORE:
			case Opcodes.CASTORE:
			case Opcodes.SASTORE:
			case Opcodes.AASTORE:
				Object arrayStoreValue = stack.pop();
				int arrayStoreIndex = (int) stack.pop();
				Object arrayStoreObj = stack.pop();
				Type type = Type.getType(arrayStoreObj.getClass());
				Class<?> primitive = PrimitiveUtils.getPrimitiveByName(type.getElementType().getClassName());
				if (primitive != null) {
					arrayStoreValue = PrimitiveUtils.castToPrimitive(arrayStoreValue, primitive);
				}
				Array.set(arrayStoreObj, arrayStoreIndex, arrayStoreValue);
				break;
			// Constants
			case Opcodes.ACONST_NULL:
				stack.push(null);
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
			case Opcodes.LCONST_0:
				stack.push(0L);
				break;
			case Opcodes.LCONST_1:
				stack.push(1L);
				break;
			case Opcodes.FCONST_0:
				stack.push(0.0f);
				break;
			case Opcodes.FCONST_1:
				stack.push(1.0f);
				break;
			case Opcodes.FCONST_2:
				stack.push(2.0f);
				break;
			case Opcodes.DCONST_0:
				stack.push(0.0D);
				break;
			case Opcodes.DCONST_1:
				stack.push(1.0D);
				break;
			// Primitive conversion
			case Opcodes.L2I:
				stack.push((int) stack.pop());
				break;
			case Opcodes.L2F:
				stack.push((float) stack.pop());
				break;
			case Opcodes.L2D:
				stack.push((double) stack.pop());
				break;
			case Opcodes.D2F:
				stack.push((float) stack.pop());
				break;
			case Opcodes.D2I:
				stack.push((int) stack.pop());
				break;
			case Opcodes.D2L:
				stack.push((long) stack.pop());
				break;
			case Opcodes.F2D:
				stack.push((double) stack.pop());
				break;
			case Opcodes.F2L:
				stack.push((long) stack.pop());
				break;
			case Opcodes.F2I:
				stack.push((int) stack.pop());
				break;
			case Opcodes.I2B:
				stack.push((byte) stack.pop());
				break;
			case Opcodes.I2C:
				stack.push(Character.toChars((int) stack.pop())[0]);
				break;
			case Opcodes.I2D:
				stack.push((double) stack.pop());
				break;
			case Opcodes.I2F:
				stack.push((float) stack.pop());
				break;
			case Opcodes.I2L:
				stack.push((long) stack.pop());
				break;
			case Opcodes.I2S:
				stack.push((short) stack.pop());
				break;
			// Math stuff
			case Opcodes.LADD:
				long ladd1 = (long) stack.pop();
				long ladd2 = (long) stack.pop();
				stack.push(ladd1 + ladd2);
				break;
			case Opcodes.LSUB:
				long lsub1 = (long) stack.pop();
				long lsub2 = (long) stack.pop();
				stack.push(lsub2 + lsub1);
				break;
			case Opcodes.LAND:
				long land1 = (long) stack.pop();
				long land2 = (long) stack.pop();
				stack.push(land2 & land1);
				break;
			case Opcodes.LCMP:
				long lcmp1 = (long) stack.pop();
				long lcmp2 = (long) stack.pop();
				stack.push(lcmp1 == lcmp2 ? 1 : 0);
				break;
			case Opcodes.LMUL:
				long lmul1 = (long) stack.pop();
				long lmul2 = (long) stack.pop();
				stack.push(lmul2 * lmul1);
				break;
			case Opcodes.LDIV:
				long ldiv1 = (long) stack.pop();
				long ldiv2 = (long) stack.pop();
				if (ldiv1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<LDIV>! Pushing 0 as temporary replacement!");
					stack.push(0L);
				} else {
					stack.push(ldiv2 / ldiv1);
				}
				break;
			case Opcodes.LNEG:
				long lneg = (long) stack.pop();
				stack.push(lneg * -1);
				break;
			case Opcodes.LOR:
				long lor1 = (long) stack.pop();
				long lor2 = (long) stack.pop();
				stack.push(lor2 | lor1);
				break;
			case Opcodes.LXOR:
				long lxor1 = (long) stack.pop();
				long lxor2 = (long) stack.pop();
				stack.push(lxor2 ^ lxor1);
				break;
			case Opcodes.LREM:
				long lrem1 = (long) stack.pop();
				long lrem2 = (long) stack.pop();
				if (lrem1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<LREM>! Pushing 0 as temporary replacement!");
					stack.push(0L);
				} else {
					stack.push(lrem2 % lrem1);
				}
				break;
			case Opcodes.LSHL:
				long lshl1 = (long) stack.pop();
				long lshl2 = (long) stack.pop();
				stack.push(lshl2 << lshl1);
				break;
			case Opcodes.LSHR:
				long lshr1 = (long) stack.pop();
				long lshr2 = (long) stack.pop();
				stack.push(lshr2 >> lshr1);
				break;
			case Opcodes.LUSHR:
				long lushr1 = (long) stack.pop();
				long lushr2 = (long) stack.pop();
				stack.push(lushr2 >>> lushr1);
				break;
			case Opcodes.DADD:
				double dadd1 = (double) stack.pop();
				double dadd2 = (double) stack.pop();
				stack.push(dadd1 + dadd2);
				break;
			case Opcodes.DSUB:
				double dsub1 = (double) stack.pop();
				double dsub2 = (double) stack.pop();
				stack.push(dsub2 + dsub1);
				break;
			case Opcodes.DMUL:
				double dmul1 = (double) stack.pop();
				double dmul2 = (double) stack.pop();
				stack.push(dmul2 * dmul1);
				break;
			case Opcodes.DDIV:
				double ddiv1 = (double) stack.pop();
				double ddiv2 = (double) stack.pop();
				if (ddiv1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<LDIV>! Pushing 0 as temporary replacement!");
					stack.push(0L);
				} else {
					stack.push(ddiv2 / ddiv1);
				}
				break;
			case Opcodes.DNEG:
				double dneg = (double) stack.pop();
				stack.push(dneg * -1);
				break;
			case Opcodes.DREM:
				double drem1 = (double) stack.pop();
				double drem2 = (double) stack.pop();
				if (drem1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<LREM>! Pushing 0 as temporary replacement!");
					stack.push(0L);
				} else {
					stack.push(drem2 % drem1);
				}
				break;
			case Opcodes.FADD:
				float fadd1 = (float) stack.pop();
				float fadd2 = (float) stack.pop();
				stack.push(fadd1 + fadd2);
				break;
			case Opcodes.FSUB:
				float fsub1 = (float) stack.pop();
				float fsub2 = (float) stack.pop();
				stack.push(fsub2 + fsub1);
				break;
			case Opcodes.FMUL:
				float fmul1 = (float) stack.pop();
				float fmul2 = (float) stack.pop();
				stack.push(fmul2 * fmul1);
				break;
			case Opcodes.FDIV:
				float fdiv1 = (float) stack.pop();
				float fdiv2 = (float) stack.pop();
				if (fdiv1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<LDIV>! Pushing 0 as temporary replacement!");
					stack.push(0L);
				} else {
					stack.push(fdiv2 / fdiv1);
				}
				break;
			case Opcodes.FNEG:
				float fneg = (float) stack.pop();
				stack.push(fneg * -1);
				break;
			case Opcodes.FREM:
				float frem1 = (float) stack.pop();
				float frem2 = (float) stack.pop();
				if (frem1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<LREM>! Pushing 0 as temporary replacement!");
					stack.push(0L);
				} else {
					stack.push(frem2 % frem1);
				}
				break;
			case Opcodes.IMUL:
				int mul1 = (int) stack.pop();
				int mul2 = (int) stack.pop();
				stack.push(mul2 * mul1);
				break;
			case Opcodes.IAND:
				int and1 = (int) stack.pop();
				int and2 = (int) stack.pop();
				stack.push(and2 & and1);
				break;
			case Opcodes.ISHL:
				int shl1 = (int) stack.pop();
				int shl2 = (int) stack.pop();
				stack.push(shl2 << shl1);
				break;
			case Opcodes.ISHR:
				int shr1 = (int) stack.pop();
				int shr2 = (int) stack.pop();
				stack.push(shr2 >> shr1);
				break;
			case Opcodes.IUSHR:
				int ushl1 = (int) stack.pop();
				int ushl2 = (int) stack.pop();
				stack.push(ushl2 >>> ushl1);
				break;
			case Opcodes.IREM:
				int rem1 = (int) stack.pop();
				int rem2 = (int) stack.pop();
				if (rem1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<IREM>! Pushing 0 as temporary replacement!");
					stack.push(0);
				} else {
					stack.push(rem2 % rem1);
				}
				break;
			case Opcodes.IOR:
				int ior1 = (int) stack.pop();
				int ior2 = (int) stack.pop();
				stack.push(ior2 | ior1);
				break;
			case Opcodes.IXOR:
				int ixor1 = (int) stack.pop();
				int ixor2 = (int) stack.pop();
				stack.push(ixor2 ^ ixor1);
				break;
			case Opcodes.ISUB:
				int sub1 = (int) stack.pop();
				int sub2 = (int) stack.pop();
				stack.push(sub2 - sub1);
				break;
			case Opcodes.INEG:
				int neg = (int) stack.pop();
				stack.push(neg * -1);
				break;
			case Opcodes.IADD:
				int add1 = (int) stack.pop();
				int add2 = (int) stack.pop();
				stack.push(add2 + add1);
				break;
			case Opcodes.IDIV:
				int div1 = (int) stack.pop();
				int div2 = (int) stack.pop();
				if (div1 == 0) {
					System.out.println("\tStackInter tried to divide by zero<IDIV>! Pushing 0 as temporary replacement!");
					stack.push(0);
				} else {
					stack.push(div2 / div1);
				}
				break;
			// Moving around objects in the stack
			case Opcodes.POP:
				stack.pop();
				break;
			case Opcodes.POP2:
				stack.pop();
				stack.pop();
				break;
			case Opcodes.SWAP:
				Object swap1 = stack.pop();
				Object swap2 = stack.pop();
				stack.push(swap1);
				stack.push(swap2);
				break;
			case Opcodes.DUP:
				// value --> value, value
				stack.push(stack.peek());
				break;
			case Opcodes.DUP_X1:
				// value2, value1 --> value1, value2, value1
				Object in1 = stack.pop();
				Object in2 = stack.pop();
				stack.push(in1);
				stack.push(in2);
				stack.push(in1);
				break;
			case Opcodes.DUP_X2:
				// value3, value2, value1 --> value1, value3, value2, value1
				Object int1 = stack.pop();
				Object int2 = stack.pop();
				Object int3 = stack.pop();
				stack.push(int1);
				stack.push(int3);
				stack.push(int2);
				stack.push(int1);
				break;
			case Opcodes.DUP2:
				// {value2, value1} --> {value2, value1}, {value2, value1}
				Object i1 = stack.pop();
				Object i2 = stack.pop();
				stack.push(i2);
				stack.push(i1);
				stack.push(i2);
				stack.push(i1);
				break;
			case Opcodes.DUP2_X1:
				// value3, {value2, value1} -->
				// {value2, value1}, value3, {value2, value1}
				Object id1 = stack.pop();
				Object id2 = stack.pop();
				Object id3 = stack.pop();
				stack.push(id2);
				stack.push(id1);
				stack.push(id3);
				stack.push(id2);
				stack.push(id1);
				break;
			case Opcodes.DUP2_X2:
				// {value4, value3}, {value2, value1} -->
				// {value2, value1}, {value4, value3}, {value2, value1}
				Object ind1 = stack.pop();
				Object ind2 = stack.pop();
				Object ind3 = stack.pop();
				Object ind4 = stack.pop();
				stack.push(ind2);
				stack.push(ind1);
				stack.push(ind4);
				stack.push(ind3);
				stack.push(ind2);
				stack.push(ind1);
				break;
			case Opcodes.LDC:
				LdcInsnNode ldc = (LdcInsnNode) ain;
				stack.push(ldc.cst);
				break;
			// Method invokes
			case Opcodes.INVOKESTATIC:
			case Opcodes.INVOKEINTERFACE:
			case Opcodes.INVOKEDYNAMIC:
			case Opcodes.INVOKEVIRTUAL:
				MethodInsnNode min = (MethodInsnNode) ain;
				DescriptionUtil data = DescriptionUtil.get(min.desc);
				List<Object> invokeArgs = new ArrayList<Object>();
				for (int i = 0; i < data.getParamCount(); i++) {
					invokeArgs.add(0, stack.pop());
				}
				// TODO: Simulate the stack for remote method call
				@SuppressWarnings("deprecation")
				Object retVal = ReflectionUtil.getValue(min.owner, min.name, data.getClassType(), invokeArgs.toArray());
				if (retVal == null) {
					System.out.println("\tStackInter encountered a method that may need invoking, but failed! " + min.desc);
				} else if (retVal instanceof Number) {
					stack.push(((Number) retVal).intValue());
				}
				break;
			default:
				System.out.println("\t\t\t\t\tUNHANDLED OPCODE: " + OpUtil.opcodes.get(ain.getOpcode()));
				break;
			}
			if (ain.getNext() == null) {
				break;
			}
			ain = ain.getNext();
		}
		return stack;
	}

}
