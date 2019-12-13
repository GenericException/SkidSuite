package me.lpk.hijack.hook;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import me.lpk.hijack.exception.BadInjectedReturnType;
import me.lpk.hijack.match.AbstractMatcher;
import me.lpk.hijack.modder.ClassModder;
import me.lpk.util.AccessHelper;

public class Hooker {
	/**
	 * Creates a modder that injects a single static method call <i>(void)</i>
	 * at the beginning of a method.
	 * 
	 * @param matcher
	 *            The to-be owner of the returned ClassModder
	 * @param methodName
	 *            Target method name
	 * @param methodDesc
	 *            Target method desc
	 * @param injectedMethodOwner
	 *            Injected method owner
	 * @param injectedMethodName
	 *            Injected method name
	 * @return
	 */
	public static ClassModder hookMethodStart(AbstractMatcher<String> matcher, String methodName, String methodDesc, String injectedMethodOwner, String injectedMethodName) {
		return hookMethodStart(matcher, methodName, methodDesc, injectedMethodOwner, injectedMethodName, "()V");
	}

	/**
	 * Creates a modder that injects a single static method call <i>(void)</i>
	 * at the end of a method.
	 * 
	 * @param matcher
	 *            The to-be owner of the returned ClassModder
	 * @param methodName
	 *            Target method name
	 * @param methodDesc
	 *            Target method desc
	 * @param injectedMethodOwner
	 *            Injected method owner
	 * @param injectedMethodName
	 *            Injected method name
	 * @return
	 */
	public static ClassModder hookMethodEnd(AbstractMatcher<String> matcher, String methodName, String methodDesc, String injectedMethodOwner, String injectedMethodName) {
		return hookMethodEnd(matcher, methodName, methodDesc, injectedMethodOwner, injectedMethodName, "()V");
	}

	/**
	 * Creates a modder that injects a static method call to the beginning of a
	 * method. If the injected method returns a value, the first parameter with
	 * a matching type will be overwritten with the returned value.
	 * 
	 * 
	 * @param matcher
	 *            The to-be owner of the returned ClassModder
	 * @param methodName
	 *            Target method name
	 * @param methodDesc
	 *            Target method desc
	 * @param injectedMethodOwner
	 *            Injected method owner
	 * @param injectedMethodName
	 *            Injected method name
	 * @param injectedMethodDesc
	 *            Injected method desc
	 * @return
	 */
	public static ClassModder hookMethodStart(AbstractMatcher<String> matcher, final String methodName, final String methodDesc, final String injectedMethodOwner, final String injectedMethodName,
			final String injectedMethodDesc) {
		return new ClassModder(matcher) {
			@Override
			public void modify(ClassNode cn) {
				for (MethodNode mn : cn.methods) {
					// Check if correct method
					if (mn.name.equals(methodName) && mn.desc.equals(methodDesc)) {
						// Inserting the method call first.
						// It will be used as an anchor for adding STORE opcodes
						// if the injected method has a valid return type.
						MethodInsnNode methodCall = new MethodInsnNode(Opcodes.INVOKESTATIC, injectedMethodOwner, injectedMethodName, injectedMethodDesc, false);
						mn.instructions.insert(methodCall);
						//
						// Get types of targeted method & method call being
						// injected
						Type injectedType = Type.getMethodType(injectedMethodDesc);
						Type targetType = Type.getMethodType(methodDesc);
						Type injectedReturnType = injectedType.getReturnType();
						//
						// Index to be overwritten with the injected method's
						// returned value
						int overrideIndex = -1;
						//
						// Iterate parameter types of the targeted method
						for (int paramIndex = 0; paramIndex < targetType.getArgumentTypes().length; paramIndex++) {
							Type targetParamType = targetType.getArgumentTypes()[paramIndex];
							// Accounting for 'this' variable in non-static
							// methods
							int varOffset = AccessHelper.isStatic(mn.access) ? 0 : 1;
							int varIndex = paramIndex + varOffset;
							// Checking params to see if it's the correct one to
							// be overwritten
							int targetParamSort = targetParamType.getSort();
							if (injectedReturnType == targetParamType) {
								overrideIndex = paramIndex;
							}
							// Insert a VarInsnNode loading the type dictated by
							// targetParamSort at the index from varIndex
							insertVarInsn(mn.instructions, true, targetParamSort, varIndex);
						}
						// If the return type is void the index will remain -1.
						if (overrideIndex != -1) {
							// Insert a VarInsnNode storing the type dictated by
							// injectedReturnType.getSort() at the index from
							// overrideIndex
							insertVarInsn(mn.instructions, false, injectedReturnType.getSort(), overrideIndex);
						}
					}
				}
			}

		};
	}

	/**
	 * Creates a modder that injects a static method call to the end of a
	 * method. If the injected method returns a value it will overwrite the
	 * target method return value.
	 * 
	 * 
	 * @param matcher
	 *            The to-be owner of the returned ClassModder
	 * @param methodName
	 *            Target method name
	 * @param methodDesc
	 *            Target method desc
	 * @param injectedMethodOwner
	 *            Injected method owner
	 * @param injectedMethodName
	 *            Injected method name
	 * @param injectedMethodDesc
	 *            Injected method desc
	 * @return
	 */
	public static ClassModder hookMethodEnd(AbstractMatcher<String> matcher, final String methodName, final String methodDesc, final String injectedMethodOwner, final String injectedMethodName,
			final String injectedMethodDesc) {
		return new ClassModder(matcher) {
			@Override
			public void modify(ClassNode cn) {
				for (MethodNode mn : cn.methods) {
					// Check if correct method
					if (mn.name.equals(methodName) && mn.desc.equals(methodDesc)) {
						// Get types of targeted method & method call being
						// injected
						Type injectedType = Type.getMethodType(injectedMethodDesc);
						Type targetType = Type.getMethodType(methodDesc);
						Type injectedReturnType = injectedType.getReturnType();
						//
						// Check if the return types of the injected and target
						// methods match
						boolean injectionHasReturn = injectedReturnType != Type.VOID_TYPE;
						if (injectionHasReturn && injectedReturnType != targetType.getReturnType()) {
							// Get the user's attention.
							throw new BadInjectedReturnType();
						}
						//
						// Determine if the method has a return opcode.
						boolean hasReturn = false;
						boolean replaceReturn = hasReturn && injectionHasReturn;
						AbstractInsnNode ret = mn.instructions.getLast();
						while (ret.getPrevious() != null) {
							if (ret.getOpcode() >= Opcodes.IRETURN && ret.getOpcode() <= Opcodes.RETURN) {
								hasReturn = true;
								break;
							}
							ret = ret.getPrevious();
						}
						//
						// If the return value is to be replaced we will need to pop the last stack value off.
						// Also after popping, we can remove the return. We'll add it back later.
						int retOpcode = Opcodes.RETURN;
						if (replaceReturn) {
							boolean doubleOrLong = injectedReturnType.getSort() == Type.DOUBLE || injectedReturnType.getSort() == Type.LONG;
							mn.instructions.insertBefore(ret, new InsnNode(doubleOrLong ? Opcodes.POP2 : Opcodes.POP));
							retOpcode = ret.getOpcode();
							mn.instructions.remove(ret);
						}
						//
						// Iterate parameter types of the targeted method
						for (int paramIndex = 0; paramIndex < targetType.getArgumentTypes().length; paramIndex++) {
							// Accounting for 'this' variable in non-static
							// methods
							int varOffset = AccessHelper.isStatic(mn.access) ? 0 : 1;
							int varIndex = paramIndex + varOffset;
							// Insert a VarInsnNode loading the type
							// dictated by
							// targetParamSort at the index from varIndex
							addVarInsn(mn.instructions, true, targetType.getSort(), varIndex);
						}
						//
						// The hook call
						mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, injectedMethodOwner, injectedMethodName, injectedMethodDesc, false));
						//
						// Re-adding the return
						if (replaceReturn) {
							mn.instructions.add(new InsnNode(retOpcode));
						}
					}
				}
			}
		};
	}

	/**
	 * Inserts a VarInsnNode at the beginning of a list of instructions
	 * <i>(Loading or storing)</i> with a given type at a given index.
	 * 
	 * @param instructions
	 *            Instructions to modify
	 * @param loading
	 *            If the var is loading vs storing
	 * @param sort
	 *            The var type
	 * @param index
	 *            The var index
	 */
	private static void insertVarInsn(InsnList instructions, boolean loading, int sort, int index) {
		switch (sort) {
		case Type.ARRAY:
		case Type.OBJECT:
			instructions.insert(new VarInsnNode(loading ? Opcodes.ALOAD : Opcodes.ASTORE, index));
			break;
		case Type.INT:
		case Type.BOOLEAN:
			instructions.insert(new VarInsnNode(loading ? Opcodes.ILOAD : Opcodes.ISTORE, index));
			break;
		case Type.FLOAT:
			instructions.insert(new VarInsnNode(loading ? Opcodes.FLOAD : Opcodes.FSTORE, index));
			break;
		case Type.DOUBLE:
			instructions.insert(new VarInsnNode(loading ? Opcodes.DLOAD : Opcodes.DSTORE, index));
			break;
		case Type.LONG:
			instructions.insert(new VarInsnNode(loading ? Opcodes.LLOAD : Opcodes.LSTORE, index));
			break;
		}
	}

	/**
	 * Adds a VarInsnNode to the end of a list of instructions <i>(Loading or
	 * storing)</i> with a given type at a given index.
	 * 
	 * @param instructions
	 *            Instructions to modify
	 * @param loading
	 *            If the var is loading vs storing
	 * @param sort
	 *            The var type
	 * @param index
	 *            The var index
	 */
	private static void addVarInsn(InsnList instructions, boolean loading, int sort, int index) {
		switch (sort) {
		case Type.ARRAY:
		case Type.OBJECT:
			instructions.add(new VarInsnNode(loading ? Opcodes.ALOAD : Opcodes.ASTORE, index));
			break;
		case Type.INT:
		case Type.BOOLEAN:
			instructions.add(new VarInsnNode(loading ? Opcodes.ILOAD : Opcodes.ISTORE, index));
			break;
		case Type.FLOAT:
			instructions.add(new VarInsnNode(loading ? Opcodes.FLOAD : Opcodes.FSTORE, index));
			break;
		case Type.DOUBLE:
			instructions.add(new VarInsnNode(loading ? Opcodes.DLOAD : Opcodes.DSTORE, index));
			break;
		case Type.LONG:
			instructions.add(new VarInsnNode(loading ? Opcodes.LLOAD : Opcodes.LSTORE, index));
			break;
		}
	}
}
