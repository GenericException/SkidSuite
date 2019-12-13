package me.lpk.gui.component.decompilers;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import me.lpk.gui.VisualizerWindow;
import me.lpk.gui.component.DecompileSelection;
import me.lpk.gui.component.SearchResultEntry;
import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappedMember;
import me.lpk.util.ASMUtils;
import me.lpk.util.OpUtils;
import me.lpk.util.ParentUtils;
import me.lpk.util.StringUtils;

public class ASMMode extends DecompileMode {
	public ASMMode() {
	}

	public ASMMode(DecompileMode mode) {
		super(mode);
	}
	
	@Override
	public void decompile(ClassNode cn, JTextPane txtEdit, StyledDocument doc) {
		ClassReader cr = new ClassReader(ASMUtils.getNodeBytes(cn, true));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		cr.accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(ps)), 0);
		String output;
		try {
			output = os.toString("UTF8");
			txtEdit.setText(output);
			SimpleAttributeSet attribClean = new SimpleAttributeSet();
			StyleConstants.setForeground(attribClean, Color.black);
			doc.setCharacterAttributes(0, output.length(), attribClean, true);
			//
			SimpleAttributeSet attribKeyword = new SimpleAttributeSet();
			SimpleAttributeSet attribComment = new SimpleAttributeSet();
			SimpleAttributeSet attribQuote = new SimpleAttributeSet();
			SimpleAttributeSet attribOpcode = new SimpleAttributeSet();
			SimpleAttributeSet attribSig = new SimpleAttributeSet();
			StyleConstants.setForeground(attribKeyword, new Color(110, 70, 160));
			StyleConstants.setForeground(attribComment, new Color(20, 100, 20));
			StyleConstants.setForeground(attribQuote, new Color(80, 80, 80));
			StyleConstants.setForeground(attribOpcode, new Color(50, 90, 120));
			StyleConstants.setForeground(attribSig, new Color(120, 30, 10));
			// StyleConstants.setItalic(set, false);
			String[] keywords = new String[] { "class", "public", "private", "protected", "static", "final", "volatile", "abstract", "synthetic", "bridge", "implements",
					"extends", "enum", "interface", "OUTERCLASS", "INNERCLASS" };
			for (String target : keywords) {
				int index = output.indexOf(target);
				while (index >= 0) {
					doc.setCharacterAttributes(index, target.length(), attribKeyword, true);
					index = output.indexOf(target, index + 1);
				}
			}
			String[] signatures = new String[] { "[", "(", "L" };
			for (String target : signatures) {
				int index = output.indexOf(target);
				while (index >= 0) {
					if (target.equals("L") && (StringUtils.isNumeric(output.substring(index + 1, index + 2)) || output.substring(index - 1, index).equals("/") || !output.substring(index - 1, index).equals(" "))) {
						index = output.indexOf(target, index + 1);
						continue;
					}
					int newline = output.substring(index).indexOf(" ");
					if (newline == -1) {
						newline = output.substring(index).indexOf("\n");
					}
					doc.setCharacterAttributes(index, newline, attribSig, true);
					index = output.indexOf(target, index + 1);
				}
			}
			Set<String> extraOpcodes = new HashSet<String>();
			extraOpcodes.addAll(Arrays.asList("LINENUMBER", "MAXSTACK", "MAXLOCALS", "FRAME", "LOCALVARIABLE", "TRYCATCHBLOCK"));
			extraOpcodes.addAll(OpUtils.getOpcodes());
			for (String opcode : extraOpcodes) {
				int i = output.indexOf(opcode);
				while (i >= 0) {
					doc.setCharacterAttributes(i, opcode.length(), attribOpcode, true);
					i = output.indexOf(opcode, i + 1);
				}
			}
			String quote = "\"";
			String singlequote = "\'";
			int i = output.indexOf(quote);
			while (i >= 0) {
				if ((output.charAt(i + 1) + "").contains(quote)) {
					i = output.indexOf(quote, i + 2);
					continue;
				}
				int quoteLen = output.substring(i + 1).indexOf(quote) + 1;
				int escapedQuote = output.substring(i + 1).indexOf("\\\"") + 2;
				int offset = 2;
				while (escapedQuote == quoteLen) {
					quoteLen = output.substring(i + offset).indexOf(quote) + 1;
					offset += 1;
				}
				if (quoteLen > 0) {
					doc.setCharacterAttributes(i, quoteLen, attribQuote, true);
				} else {
					quoteLen = 1;
				}
				i = output.indexOf(quote, i + quoteLen + 1);
			}
			i = output.indexOf(singlequote);
			while (i >= 0) {
				if ((output.charAt(i + 1) + "").contains(singlequote)) {
					i = output.indexOf(singlequote, i + 2);
					continue;
				}
				int quoteLen = output.substring(i + 1).indexOf(singlequote) + 1;
				int escapedQuote = output.substring(i + 1).indexOf("\\\'") + 2;
				int offset = 2;
				while (escapedQuote == quoteLen) {
					quoteLen = output.substring(i + offset).indexOf(singlequote) + 1;
					offset += 1;
				}
				if (quoteLen > 0) {
					doc.setCharacterAttributes(i, quoteLen, attribQuote, true);
				} else {
					quoteLen = 1;
				}
				i = output.indexOf(singlequote, i + quoteLen + 1);
			}
			String comment = "//";
			i = output.indexOf(comment);
			while (i >= 0) {
				int newline = output.substring(i).indexOf("\n");
				doc.setCharacterAttributes(i, newline, attribComment, true);
				i = output.indexOf(comment, i + 1);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DecompileSelection getSelection(JTextPane txtEdit) {
		String text = txtEdit.getSelectedText();
		if (text == null || text.length() <= 0) {
			return null;
		}
		int start = txtEdit.getSelectionStart(), end = txtEdit.getSelectionEnd(), i = 0, ix = 0;
		String tmp = text, tmp2 = text;
		while (!tmp.contains("\n")) {
			try {
				tmp += txtEdit.getText(end + i, 1);
				i += 1;
			} catch (BadLocationException e) {
				e.printStackTrace();
				break;
			}
		}
		boolean forwards = true;
		while (!tmp2.contains(" ")) {
			try {
				if (forwards) {
					String next = txtEdit.getText(end + ix, 1);
					if (!next.equals(" ")) {
						tmp2 += next;
					} else {
						forwards = false;
						ix = 0;
					}
				} else {
					String next = txtEdit.getText(start - ix, 1);
					if (next.equals(" ")) {
						break;
					} else {
						tmp2 = next + tmp2;
					}
				}
				ix += 1;
			} catch (BadLocationException e) {
				e.printStackTrace();
				break;
			}
		}
		int i2 = 1;
		while (!tmp.substring(0, tmp.length() - 1).contains("\n")) {
			try {
				tmp = txtEdit.getText(start - i2, 1) + tmp;
				i2 += 1;
			} catch (BadLocationException e) {
				e.printStackTrace();
				break;
			}
		}
		boolean containsTemp = nodes.containsKey(tmp2);
		boolean containsExact = nodes.containsKey(text);
		if (tmp.contains(" class ") || tmp.contains(" interface ") || containsExact || containsTemp) {
			if (containsTemp) {
				return new DecompileSelection(DecompileSelection.SelectionType.Class, text, nodes.get(tmp2));
			} else if (containsExact) {
				return new DecompileSelection(DecompileSelection.SelectionType.Class, text, nodes.get(text));
			} else {
				return new DecompileSelection(DecompileSelection.SelectionType.Class, text, currNode);
			}
		}
		if (tmp.contains("(")) {
			if (tmp.contains("INVOKESTATIC") || tmp.contains("INVOKEINTERFACE") || tmp.contains("INVOKEVIRTUAL") || tmp.contains("INVOKESPECIAL")) {
				// Get method's owner from tmp
				ClassNode owner = classFromLine(tmp);
				return new DecompileSelection(DecompileSelection.SelectionType.Method, text, owner, methodFromLine(owner, tmp));
			} else {
				return new DecompileSelection(DecompileSelection.SelectionType.Method, text, currNode, methodFromLine(tmp));
			}
		} else {
			if (tmp.contains("\"")) {
				if (!tmp.endsWith("\"")) {
					// When the entire string is selected it just ignroes the
					// last character. Ugly fix.
					try {
						tmp += txtEdit.getText(end, 1);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				// Fixing indexes for ease of searching.
				start = tmp.indexOf(text);
				end = start + text.length() + 1;
				if (tmp.indexOf("\"") <= start && end <= tmp.lastIndexOf("\"")) {
					return new DecompileSelection(DecompileSelection.SelectionType.String, text, currNode);
				}
			}
			if (tmp.contains("GETSTATIC") || tmp.contains("PUTSTATIC") || tmp.contains("GETFIELD") || tmp.contains("PUTFIELD")) {
				// Get method's owner from tmp
				ClassNode owner = classFromLine(tmp);
				return new DecompileSelection(DecompileSelection.SelectionType.Field, text, classFromLine(tmp), fieldFromLine(owner, tmp));
			} else if (tmp.contains("NEW")) {
				return new DecompileSelection(DecompileSelection.SelectionType.Class, text, classFromLine(tmp));
			} else {
				return new DecompileSelection(DecompileSelection.SelectionType.Field, text, currNode, fieldFromLine(tmp));
			}
		}
	}
	
	@Override
	public  void find(SearchResultEntry result, JTextPane txtEdit) {
		int j = -1, k = 1;
		if (result.isMethodResult()){
			String name = result.getMethod().name;
			String desc = result.getMethod().desc;
			j = txtEdit.getText().indexOf(name + desc);
			k = name.length();
		}else{
			String name = result.getField().name;
			String desc = result.getField().desc;
			j = txtEdit.getText().indexOf(desc + " " + name);
			k = name.length();
		}
		// Final for the runnable
		final int foundIndex = j, len = k;
		if (foundIndex >= 0){
			txtEdit.setCaretPosition(txtEdit.getText().length() - 1);
			// Again with the threading shit because setting the caret position
			// instantly after another set doesn't work.
			// This delay will force the found result to the top of the page.
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					txtEdit.setCaretPosition(foundIndex);
					txtEdit.setSelectionStart(foundIndex);
					txtEdit.setSelectionEnd(foundIndex + len); 
				}
			}.start();
		}
	}

	protected MethodNode methodFromLine(ClassNode owner, String tmp) {
		// Clean the input and split by spaces
		String[] split = tmp.trim().split(" ");
		if (split.length < 3 || owner == null) {
			return null;
		}
		String name = split[1];
		String desc = split[2];

		// should be className.object
		// So cut everything before '.'
		String methodStr = name.substring(name.indexOf(".") + 1);
		Map<String, MappedClass> mappings = VisualizerWindow.instance.getMappings();
		MappedMember member = ParentUtils.findMethod(mappings.get(owner.name), methodStr, desc, true);
		if (member != null) {
			return member.getMethodNode();
		}
		return null;
	}

	protected FieldNode fieldFromLine(ClassNode owner, String tmp) {
		// Clean the input and split by spaces
		String[] split = tmp.trim().split(" ");
		if (split.length < 3 || owner == null) {
			return null;
		}
		String name = split[split.length - 3];
		String desc = split[split.length - 1];
		// should be className.object
		// So cut everything before '.'
		name = name.substring(name.indexOf(".") + 1);
		Map<String, MappedClass> mappings = VisualizerWindow.instance.getMappings();
		MappedMember member = ParentUtils.findField(mappings.get(owner.name), name, desc);
		if (member != null) {
			return member.getFieldNode();
		}
		return null;
	}

	protected MethodNode methodFromLine(String tmp) {
		// Clean the input and split by spaces
		if (tmp.contains("throws")){
			tmp = tmp.substring(0, tmp.lastIndexOf(" throws"));
		}
		String[] split = tmp.trim().split(" ");
		if (split.length < 2) {
			return null;
		}
		String data = split[split.length - 1];
		String name = data.substring(0, data.indexOf("("));
		String desc = data.substring(data.indexOf("("));
		Map<String, MappedClass> mappings = VisualizerWindow.instance.getMappings();
		MappedMember member = ParentUtils.findMethod(mappings.get(currNode.name), name, desc, true);
		if (member != null) {
			return member.getMethodNode();
		}
		return null;
	}

	protected FieldNode fieldFromLine(String tmp) {
		// Clean the input and split by spaces
		String[] split = tmp.trim().split(" ");
		if (split.length < 3) {
			return null;
		}
		boolean hasValue = tmp.contains("=");
		String name = split[split.length - (hasValue ? 3 : 1)];
		String desc = split[split.length - (hasValue ? 4 : 2)];
		// should be className.object
		// So cut everything before '.'
		Map<String, MappedClass> mappings = VisualizerWindow.instance.getMappings();
		MappedMember member = ParentUtils.findField(mappings.get(currNode.name), name, desc);
		if (member != null) {
			return member.getFieldNode();
		}
		return null;
	}

	/**
	 * Gets the owner in the given line. Returns null if the owner has not been
	 * loaded as a ClassNode.
	 * 
	 * @param line
	 * @return
	 */
	protected ClassNode classFromLine(String line) {
		// Clean the input and split by spaces
		String[] split = line.trim().split(" ");
		String s = split[1];
		// should be className.object
		// So cut up to '.'
		int dotIndex = s.indexOf(".");
		String classStr = s;
		if (dotIndex != -1) {
			classStr = s.substring(0, dotIndex);
		}
		return nodes.get(classStr);
	}
}
