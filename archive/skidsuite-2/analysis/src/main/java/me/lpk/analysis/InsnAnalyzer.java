package me.lpk.analysis;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.tree.*;

import java.util.List;


public class InsnAnalyzer extends Analyzer<InsnValue> {

	public static InsnAnalyzer create(StackHelper helper) {
		return new InsnAnalyzer(new InsnInterpreter(helper));
	}


	private InsnAnalyzer(InsnInterpreter interpreter) {
		super(interpreter);
	}

	@Override
	protected StackFrame newFrame(int nLocals, int nStack) {
		return new StackFrame(nLocals, nStack);
	}

	@Override
	protected StackFrame newFrame(Frame<? extends InsnValue> frame) {
		return  new StackFrame(frame);
	}

	private static class InsnInterpreter extends Interpreter<InsnValue> {
		private final StackHelper helper;

		protected InsnInterpreter(StackHelper helper) {
			super(Opcodes.ASM7);
			this.helper = helper;
		}

		@Override
		public InsnValue newValue(Type type) {
			return helper.newValue(type);
		}

		@Override
		public InsnValue newOperation(AbstractInsnNode ain) throws AnalyzerException {
			return helper.createConstant(ain);
		}

		@Override
		public InsnValue copyOperation(AbstractInsnNode ain, InsnValue v1) throws AnalyzerException {
			return v1;
		}

		@Override
		public InsnValue unaryOperation(AbstractInsnNode ain, InsnValue v1) throws AnalyzerException {
			int op = ain.getOpcode();
			if (op >= INEG && op <= DNEG)
				return helper.invertValue(ain, v1);
			else if (op == IINC)
				return helper.incrementLocal((IincInsnNode) ain, v1);
			else if (op >= I2L && op <= I2S)
				return helper.convertValue(ain, v1);
			else if (op == GETFIELD)
				return helper.getField((FieldInsnNode) ain, v1);
			else if (op == NEWARRAY || op == ANEWARRAY || op == ARRAYLENGTH)
				return helper.array(ain, v1);
			else if (op == CHECKCAST || op == INSTANCEOF)
				return helper.casting((TypeInsnNode) ain, v1);
			/*
			ATHROW:
			IFEQ:
			IFNE:
			IFLT:
			IFGE:
			IFGT:
			IFLE:
			TABLESWITCH:
			LOOKUPSWITCH:
			IRETURN:
			LRETURN:
			FRETURN:
			DRETURN:
			ARETURN:
			PUTSTATIC:
			 */
			return null;
		}

		@Override
		public InsnValue binaryOperation(AbstractInsnNode ain, InsnValue v1, InsnValue v2) throws AnalyzerException {
			int op = ain.getOpcode();
			if (op >= IALOAD && op <= SALOAD)
				return helper.loadFromArray(ain, v1, v2);
			if (op >= IADD && op <= DREM)
				return helper.doMath(ain, v1, v2);
			if (op >= ISHL && op <= LXOR)
				return helper.doMath(ain, v1, v2);
			if (op >= LCMP && op <= DCMPG)
				return helper.compareConstants(ain, v1, v2);
			if (op >= IF_ICMPEQ && op <= IF_ACMPNE)
				return helper.compareConstants(ain, v1, v2);
			return null;
		}

		@Override
		public InsnValue ternaryOperation(AbstractInsnNode ain, InsnValue v1,
										  InsnValue v2, InsnValue v3) throws AnalyzerException {
			// Intentionally null
			return null;
		}

		@Override
		public InsnValue naryOperation(AbstractInsnNode ain,
									   List<? extends InsnValue> list) throws AnalyzerException {
			int op = ain.getOpcode();
			List<InsnValue> types = (List<InsnValue>) list;
			if (op == MULTIANEWARRAY)
				return helper.onMultiANewArray((MultiANewArrayInsnNode) ain, types);
			else if (op >= INVOKEVIRTUAL && op <= INVOKEDYNAMIC)
				return helper.onMethod(ain, types);
			return null;
		}

		@Override
		public void returnOperation(AbstractInsnNode ain, InsnValue value,
									InsnValue expected) throws AnalyzerException {
			// TODO: Record returns?
		}

		@Override
		public InsnValue merge(InsnValue v1, InsnValue v2) {
			// TODO: What to do?
			// - This is fine for primitives
			// - but for types, it needs to be a "common type"
			return v1;
		}
	}
}