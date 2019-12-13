package me.lpk.gui.component;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class SearchResultEntry {
	private final ClassNode target;
	private final MethodNode method;
	private final FieldNode field;
	private final int opcodeIndex;
	private final String display;

	public SearchResultEntry(ClassNode target) {
		this(target, null, 0);
	}

	public SearchResultEntry(ClassNode target, FieldNode field) {
		this.target = target;
		this.method = null;
		this.field = field;
		this.opcodeIndex = -1;
		this.display = target.name + "#" + field.name + " " + field.desc;
	}

	public SearchResultEntry(ClassNode target, MethodNode method, int opcodeIndex) {
		this.target = target;
		this.method = method;
		this.field = null;
		this.opcodeIndex = opcodeIndex;
		this.display = method != null ? target.name + "#" + method.name + (opcodeIndex == -1 ? "" : "@" + opcodeIndex) : target.name;
	}

	/**
	 * Get the MethodNode the result was found in.
	 * 
	 * @return
	 */
	public MethodNode getMethod() {
		return method;
	}

	/**
	 * Get the FieldNode the result points to.
	 * 
	 * @return
	 */
	public FieldNode getField() {
		return field;
	}

	/**
	 * Returns true if the result is pointing to a MethodNode.
	 * 
	 * @return
	 */
	public boolean isMethodResult() {
		return method != null;
	}

	/**
	 * Returns true if the result is pointing to a FieldNode.
	 * 
	 * @return
	 */
	public boolean isFieldResult() {
		return field != null;
	}

	/**
	 * Get the index of the opcode the result was found at.
	 * 
	 * @return
	 */
	public int getOpcodeIndex() {
		return opcodeIndex;
	}

	/**
	 * Get the ClassNode the result was found in.
	 * 
	 * @return
	 */
	public ClassNode getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return display;
	}
}