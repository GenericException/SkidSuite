package me.lpk.gui.component;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class DecompileSelection {
	private final SelectionType type;
	private final String text;
	private final ClassNode node;
	private final MethodNode method;
	private final FieldNode field;

	public DecompileSelection(SelectionType type, String text, ClassNode node, MethodNode method, FieldNode field) {
		this.type = type;
		this.text = text;
		this.node = node;
		this.method = method;
		this.field = field;
	}

	public DecompileSelection(SelectionType type, String text, ClassNode node) {
		this(type, text, node, null, null);
	}

	public DecompileSelection(SelectionType type, String text, ClassNode node, FieldNode field) {
		this(type, text, node, null, field);
	}

	public DecompileSelection(SelectionType type, String text, ClassNode node, MethodNode method) {
		this(type, text, node, method, null);
	}

	public enum SelectionType {
		Class, Field, Method, String;
	}

	public SelectionType getType() {
		return type;
	}

	public boolean isClass() {
		return type == SelectionType.Class;
	}

	public boolean isField() {
		return type == SelectionType.Field;
	}

	public boolean isMethod() {
		return type == SelectionType.Method;
	}

	public boolean isString() {
		return type == SelectionType.String;
	}

	public String getSelection() {
		return text;
	}

	public ClassNode getNode() {
		return node;
	}

	public FieldNode getField() {
		return field;
	}

	public MethodNode getMethod() {
		return method;
	}
}
