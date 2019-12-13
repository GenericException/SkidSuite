package me.lpk.gui.component.decompilers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.gui.component.DecompileSelection;
import me.lpk.gui.component.SearchResultEntry;

public abstract class DecompileMode {
	protected ClassNode currNode;
	protected File jarFile;
	protected final Map<String, ClassNode> nodes = new HashMap<String, ClassNode>();
	public DecompileMode(){
		
	}
	public DecompileMode(DecompileMode mode) {
		currNode = mode.currNode;
		jarFile = mode.jarFile;
		nodes.putAll(mode.nodes);
	}
	
	public abstract void decompile(ClassNode cn, JTextPane txtEdit, StyledDocument doc);
	public abstract DecompileSelection getSelection(JTextPane txtEdit);
	public abstract void find(SearchResultEntry result, JTextPane txtEdit);
	
	public void updateNodes(Map<String, ClassNode> nodes) {
		this.nodes.clear();
		this.nodes.putAll(nodes);
	}

	public void updateCurrentNode(ClassNode cn) {
		currNode = cn;
	}

	public void upateFile(File file) {
		jarFile = file;
	}
}
