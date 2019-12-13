package me.lpk.gui.component.decompilers;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.objectweb.asm.tree.ClassNode;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import org.objectweb.asm.Type;

import me.lpk.gui.component.DecompileSelection;
import me.lpk.gui.component.SearchResultEntry;
import me.lpk.util.RegexUtils;

public class ProcyonMode extends DecompileMode {
	public ProcyonMode() {
	}

	public ProcyonMode(DecompileMode mode) {
		super(mode);
	}

	@Override
	public void decompile(ClassNode cn, JTextPane txtEdit, StyledDocument doc) {
		try {
			JarTypeLoader jtl = new JarTypeLoader(new JarFile(jarFile));
			PlainTextOutput pto = new PlainTextOutput();
			DecompilerSettings ds = DecompilerSettings.javaDefaults();
			ds.setTypeLoader(jtl);
			Decompiler.decompile(cn.name, pto, ds);
			String output = pto.toString();
			txtEdit.setText(output);
			// Begin syntax highlighting
			SimpleAttributeSet attribClean = new SimpleAttributeSet();
			StyleConstants.setForeground(attribClean, Color.black);
			doc.setCharacterAttributes(0, output.length(), attribClean, true);
			SimpleAttributeSet attribKeyword = new SimpleAttributeSet();
			SimpleAttributeSet attribComment = new SimpleAttributeSet();
			SimpleAttributeSet attribQuote = new SimpleAttributeSet();
			SimpleAttributeSet attribConst = new SimpleAttributeSet();
			SimpleAttributeSet attribClass = new SimpleAttributeSet();
			StyleConstants.setForeground(attribKeyword, new Color(110, 70, 160));
			StyleConstants.setForeground(attribComment, new Color(20, 100, 20));
			StyleConstants.setForeground(attribQuote, new Color(80, 80, 80));
			StyleConstants.setForeground(attribConst, new Color(90, 130, 180));
			StyleConstants.setForeground(attribClass, new Color(160, 140, 40));

			String[] keywords = new String[] { "class", "instanceof", "public", "private", "protected", "static", "final", "volatile", "abstract", "synthetic", "bridge",
					"implements", "void", "try", "catch", "default", "for", "while", "case", "switch", "if", "else", "extends", "byte", "enum", "interface", "true",
					"false", "null", "int", "double", "float", "boolean", "char", "long", "this", "new", "return", "break", "continue", "import" };
			for (String target : keywords) {
				int index = output.indexOf(target);
				while (index >= 0) {
					if (isNotAlphabetic(output.charAt(index - 1)) && isNotAlphabetic(output.charAt(index + target.length()))) {
						doc.setCharacterAttributes(index, target.length(), attribKeyword, true);
					}
					index = output.indexOf(target, index + 1);
				}
			}

			for (String node : nodes.keySet()) {
				String target = node.contains("/") ? node.substring(node.lastIndexOf("/") + 1) : node;
				if (target.length() <= 3){
					continue;
				}
				int index = output.indexOf(target);
				while (index >= 0) {
					if (isNotAlphabetic(output.charAt(index - 1)) && isNotAlphabetic(output.charAt(index + target.length()))) {
						doc.setCharacterAttributes(index, target.length(), attribClass, true);
					}
					index = output.indexOf(target, index + 1);
				}
			}

			String quote = "\"";
			String singlequote = "\'";
			String multiComment = "/*", multiCommentEnd = "*/";
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
			i = output.indexOf(multiComment);
			while (i >= 0) {
				int quoteLen = output.substring(i + 1).indexOf(multiCommentEnd) + 2;
				if (quoteLen > 0) {
					doc.setCharacterAttributes(i, quoteLen, attribComment, true);
				} else {
					quoteLen = 1;
				}
				i = output.indexOf(multiComment, i + quoteLen + 1);
			}

			List<String> numbers = RegexUtils.matchNumbers(output);
			for (String target : numbers) {
				int index = output.indexOf(target);
				while (index >= 0) {
					doc.setCharacterAttributes(index, target.length(), attribConst, true);
					index = output.indexOf(target, index + 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DecompileSelection getSelection(JTextPane txtEdit) {
		// Since for the most part you just can't easily get what class a field
		// belongs to this is gonna be null for now.
		//
		// TODO: Use black magic to get this working like the ASM mode.
		return null;
	}

	@Override
	public void find(SearchResultEntry result, JTextPane txtEdit) {
		String desc = null;
		String name = null;
		if (result.isMethodResult()){
			 name = result.getMethod().name;
			desc = result.getMethod().desc;
			desc = desc.substring(desc.lastIndexOf(")") + 1).replace("[", "");
			// Searching in signature
			if (desc.length() > 1) {
				List<String> matches = RegexUtils.matchDescriptionClasses(desc);
				if (matches.size() > 0) {
					desc = matches.get(0);
					desc = desc.substring(desc.lastIndexOf("/") + 1);
				}
			}
			
		}else{
			 name = result.getField().name;
			desc = Type.getType(result.getField().desc).getInternalName();
		}			

		
		if (desc.length() == 1) {
			switch (desc) {
			case "V":
				desc = "void";
				break;
			case "J":
				desc = "long";
				break;
			case "Z":
				desc = "boolean";
				break;
			case "I":
				desc = "int";
				break;
			case "D":
				desc = "double";
				break;
			case "F":
				desc = "float";
				break;
			case "B":
				desc = "byte";
				break;
			case "C":
				desc = "char";
				break;
			case "S":
				desc = "short";
				break;
			}
		} else {
			
		}
		int k = 0;
		
		// Copy so the anonymous thread can access it...
		Pattern pattern = Pattern.compile("(" + desc + ").*(" + name + ")");
		Matcher m = pattern.matcher(txtEdit.getText());
		boolean found = m.find();
		int foundIndex = found ? m.start() : -1;
		final int len = desc.length() + 1, lenName = name.length();
		if (foundIndex >= 0) {
			txtEdit.setCaretPosition(txtEdit.getText().length() - 1);
			// Again with the threading shit becase setting the caret positon
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
					txtEdit.setSelectionStart(foundIndex  + len);
					txtEdit.setSelectionEnd(foundIndex  + len + lenName); 
				}
			}.start();
		}
	}

	private boolean isNotAlphabetic(char charAt) {
		String s = charAt + "";
		Pattern regex = Pattern.compile("[^A-Za-z0-9]");
		Matcher m = regex.matcher(s);
		return m.find();
	}

}
