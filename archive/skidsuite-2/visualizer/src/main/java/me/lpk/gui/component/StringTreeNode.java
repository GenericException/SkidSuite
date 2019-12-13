package me.lpk.gui.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;

public class StringTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private final Map<String, StringTreeNode> children = new HashMap<String, StringTreeNode>();
	private final String path;
	public StringTreeNode(String text, String path) {
		super(text);
		this.path = path;
	}

	public void remove(final boolean isTop) {
		final Map<String, StringTreeNode> children = this.children;
		final Iterator<StringTreeNode> iter;
		if ((iter = ((children != null) ? children.values().iterator() : null)) != null) {
			while (iter.hasNext()) {
				final StringTreeNode child;
				if ((child = iter.next()).isLeaf()) {
					iter.remove();
					child.remove(false);
				} else {
					iter.remove();
					child.remove(false);
				}
			}
		}
		if (isTop) {
			System.err.println("WHAT");
		}
	}

	public StringTreeNode getChild(String s) {
		return children.get(s);
	}

	public void addChild(String s, StringTreeNode n) {
		children.put(s, n);
	}

	public String getPathStr() {
		return path;
	}
}
