package me.lpk.mapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.mapping.objects.MappedClass;
import me.lpk.mapping.objects.MappedField;
import me.lpk.mapping.objects.MappedMethod;

public class EnigmaLoader {
	private final Map<String, ClassNode> nodes;

	public EnigmaLoader(Map<String, ClassNode> nodes) {
		this.nodes = nodes;
	}

	public Map<String, MappedClass> read(FileReader in) {
		try {
			return read(new BufferedReader(in));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, MappedClass> read(BufferedReader fileReader) throws Exception {
		Map<String, MappedClass> remap = new HashMap<String, MappedClass>();
		int lineNumber = 0;
		String line = null;
		MappedClass curClass = null;
		while ((line = fileReader.readLine()) != null) {
			lineNumber++;
			int commentPos = line.indexOf('#');
			if (commentPos >= 0) {
				line = line.substring(0, commentPos);
			}
			if (line.trim().length() <= 0) {
				continue;
			}
			int indent = 0;
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) != '\t') {
					break;
				}
				indent++;
			}
			String[] parts = line.trim().split("\\s");
			try {
				// read the first token
				String token = parts[0];
				if (token.equalsIgnoreCase("CLASS")) {
					if (indent <= 0) {
						// outer class
						curClass = readClass(parts);
						remap.put(curClass.getOriginal(), curClass);
					} else {
						// inner class
						/*
						 * if (!(mappingStack.peek() instanceof MappedClass))
						 * throw new Exception( "Unexpected CLASS entry (Line: "
						 * + lineNumber + " )" ); classMapping =
						 * readClass(parts, true); ((MappedClass)
						 * mappingStack.peek()).addInnerClassMapping(
						 * classMapping);
						 */
					}
				} else if (token.equalsIgnoreCase("FIELD")) {
					if (curClass == null) {
						throw new Exception("Unexpected FIELD entry (Line: " + lineNumber + " )");
					}
					addField(curClass, parts);
				} else if (token.equalsIgnoreCase("METHOD")) {
					if (curClass == null) {
						throw new Exception("Unexpected METHOD entry (Line: " + lineNumber + " )");
					}
					addMethod(curClass, parts);
				} else if (token.equalsIgnoreCase("ARG")) {
					// SkidGUI does not map method args yet.
					if (curClass == null) {
						throw new Exception("Unexpected ARG entry (Line: " + lineNumber + " )");
					}
				}
			} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
				throw new Exception("Malformed line:\n" + line);
			}
		}
		// Fixing the MappedClass's parent / child structure.
		for (String className : remap.keySet()) {
			MappedClass classMap = remap.get(className);
			// MappedClass has no parent.
			if (classMap.getParent() == null) {
				// Find its parent.
				MappedClass parent = remap.get(classMap.getNode().superName);
				// If found, set it's parent. Have the parent set it as its
				// child.
				if (parent != null) {
					classMap.setParent(parent);
					parent.addChild(classMap);
				}
			} else { // MappedClass has parent.
				// If the parent does not have it as a child, add it.
				if (!classMap.getParent().getChildren().contains(classMap)) {
					classMap.getParent().addChild(classMap);
				}
			}
		}
		return remap;
	}

	/**
	 * Generating mapping for a class.
	 * 
	 * @param parts
	 * @return
	 */
	private MappedClass readClass(String[] parts) {
		String original = parts[1];
		if (original.startsWith("none/")) {
			original = original.substring("none/".length());
		}
		MappedClass mc = null;
		if (parts.length == 2) {
			mc = new MappedClass(nodes.get(original), original, null);
		} else if (parts.length == 3) {
			String newName = parts[2];
			mc = new MappedClass(nodes.get(original), newName, null);
		}
		return mc;
	}

	/**
	 * Add a field to the given class.
	 * 
	 * @param clazz
	 * @param parts
	 */
	private void addField(MappedClass clazz, String[] parts) {
		String original = "";
		String newName = "";
		String desc = "";
		if (parts.length == 3) {
			original = parts[1];
			newName = parts[1];
			desc = parts[2];
		} else if (parts.length == 4) {
			original = parts[1];
			newName = parts[2];
			desc = parts[3];
		}else{
			return;
		}

		MappedField mf = new MappedField(original, newName, desc);
		if (clazz.getFields().get(original) == null) {
			clazz.getFields().put(original, new HashMap<String, MappedField>());
		}
		clazz.getFields().get(original).put(desc, mf);
	}

	/**
	 * Add a method to the given class.
	 * 
	 * @param clazz
	 * @param parts
	 */
	private void addMethod(MappedClass clazz, String[] parts) {
		String original = "";
		String newName = "";
		String desc = "";
		if (parts.length == 3) {
			original = parts[1];
			newName = parts[1];
			desc = parts[2];
		} else if (parts.length == 4) {
			original = parts[1];
			newName = parts[2];
			desc = parts[3];
		}else{
			return;
		}
		MappedMethod mm = new MappedMethod(original, newName, desc);
		if (clazz.getMethods().get(original) == null) {
			clazz.getMethods().put(original, new HashMap<String, MappedMethod>());
		}
		clazz.getMethods().get(original).put(desc, mm);
	}
}
