package me.lpk.mapping;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import me.lpk.mapping.objects.MappedClass;
import me.lpk.mapping.objects.MappedField;
import me.lpk.mapping.objects.MappedMethod;
import me.lpk.util.ASMUtil;
import me.lpk.util.AccessHelper;

/**
 * TODO: Make less of the functionality static. Was fine for testing but it
 * should be fixed for release. Get ready for a sort of future plugin system.
 */
public class MappingGen {
	private static MappingMode mMode;
	private static Map<String, ClassNode> nodes;
	private static Map<String, MappedClass> rename;
	private static File lastUsed;
	private static String mainClass;

	/**
	 * Remaps a map of <String(Class names), ClassNode>.
	 * 
	 * @param nameMode
	 * @param nodes
	 * @return
	 */
	public static Map<String, MappedClass> getRename(MappingMode mode, Map<String, ClassNode> nodess) {
		mMode = mode;
		nodes = nodess;
		rename = new HashMap<String, MappedClass>();
		for (ClassNode cn : nodes.values()) {
			map(cn);
		}
		for (String className : rename.keySet()) {
			MappedClass classMap = rename.get(className);
			// MappedClass has no parent.
			if (classMap.getParent() == null) {
				// Find its parent.
				MappedClass parent = rename.get(classMap.getNode().superName);
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
		boolean log = false;
		if (log) {
			Genson g = new GensonBuilder().useIndentation(false).setSkipNull(true).exclude(ClassNode.class).exclude("parent", MappedClass.class).exclude("children", MappedClass.class).exclude("fields", MappedClass.class).create();
			System.out.println(g.serialize(rename).trim());
		}
		return rename;
	}

	/**
	 * Create mapping for a given node. Checks if the given node has parents and
	 * maps those before mapping the node given.
	 * 
	 * @param cn
	 */
	private static void map(ClassNode cn) {
		boolean hasParents = !cn.superName.equals("java/lang/Object");
		boolean hasInterfaces = cn.interfaces.size() > 0;
		if (hasParents) {
			boolean parentRenamed = rename.containsKey(cn.superName);
			ClassNode parentNode = nodes.get(cn.superName);
			if (parentNode != null && !parentRenamed) {
				map(parentNode);
			}
		}
		if (hasInterfaces) {
			for (String interfaze : cn.interfaces) {
				boolean interfaceRenamed = rename.containsKey(interfaze);
				ClassNode interfaceNode = nodes.get(interfaze);
				if (interfaceNode != null && !interfaceRenamed) {
					map(interfaceNode);
				}
			}
		}
		boolean isRenamed = rename.containsKey(cn.name);
		if (!isRenamed) {
			mapClass(cn);
		}
	}

	private static void mapClass(ClassNode cn) {
		MappedClass classMap = new MappedClass(cn, mMode.getClassName(cn), rename.get(cn.superName));
		addFields(classMap);
		addMethods(classMap);
		rename.put(cn.name, classMap);
	}

	private static void addFields(MappedClass classMap) {
		for (FieldNode fieldNode : classMap.getNode().fields) {
			MappedField mappedField = new MappedField(fieldNode.name, mMode.getFieldName(fieldNode), fieldNode.desc);
			if (classMap.getFields().get(fieldNode.name) == null) {
				classMap.getFields().put(fieldNode.name, new HashMap<String, MappedField>());
			}
			classMap.getFields().get(fieldNode.name).put(fieldNode.desc, mappedField);
		}
	}

	private static void addMethods(MappedClass classMap) {
		for (MethodNode methodNode : classMap.getNode().methods) {
			MappedMethod mappedMethod = null;
			// If the method is the main one, make sure it's output name is the
			// same as the intial name. Mark it as the main method as well.
			if (isMain(methodNode)) {
				mappedMethod = new MappedMethod(methodNode.name, methodNode.name, methodNode.desc);
				setMain(classMap.getRenamed());
			} else if (methodNode.name.contains("<")) {
				// If the name is <init> or <clinit>
				mappedMethod = new MappedMethod(methodNode.name, methodNode.name, methodNode.desc);
			} else if (AccessHelper.isSynthetic(methodNode.access)) {
				// The method is synthetic. It most likely should not be
				// renamed.
				mappedMethod = new MappedMethod(methodNode.name, methodNode.name, methodNode.desc);
			} else {
				// If the method is not the main method and not <init>/<clinit>,
				// attempt to find it in a parent class.
				mappedMethod = getParentMethod(classMap, methodNode);
			}

			// If the method belongs to an enum and is an inbuilt method
			// belonging to the Enum class.
			if (mappedMethod == null && AccessHelper.isEnum(classMap.getNode().access)) {
				if (methodNode.name.equals("values") || methodNode.name.equals("getName") || methodNode.name.equals("ordinal")) {
					mappedMethod = new MappedMethod(methodNode.name, methodNode.name, methodNode.desc);
				}
			}
			// If the method is still null, attempt to find it in
			// the interfaces.
			if (mappedMethod == null) {
				mappedMethod = getInterfaceMethod(classMap, methodNode);
			}
			// Use reflection to see if a parent class has the method
			if (mappedMethod == null) {
				mappedMethod = getParentBackup(classMap, methodNode);
			}
			// If the method is STILL null this means it must be totally
			// new. Obfuscate it.
			if (mappedMethod == null) {
				mappedMethod = new MappedMethod(methodNode.name, mMode.getMethodName(methodNode), methodNode.desc);
			}
			// Add the method to the mapped class.
			if (classMap.getMethods().get(methodNode.name) == null) {
				classMap.getMethods().put(methodNode.name, new HashMap<String, MappedMethod>());
			}
			classMap.getMethods().get(methodNode.name).put(methodNode.desc, mappedMethod);
		}
	}

	/**
	 * Attempt to find the given method in a parent class, given the inital
	 * class the method belongs do.
	 * 
	 * @param classMap
	 *            Initial class
	 * @param methodNode
	 *            Initial method
	 * @return
	 */
	private static MappedMethod getParentMethod(final MappedClass classMap, final MethodNode methodNode) {
		MappedClass parentMap = classMap.getParent();
		while (parentMap != null) {
			if (parentMap.getMethods().containsKey(methodNode.name)) {
				Map<String, MappedMethod> methods = parentMap.getMethodByName(methodNode.name);
				if (methods != null) {
					MappedMethod mm = methods.get(methodNode.desc);
					if (mm != null) {
						return mm;
					}
				}
			}
			parentMap = parentMap.getParent();
		}
		return null;
	}

	/**
	 * Attempt to find the given method in an interface, given the inital class
	 * the method belongs do.
	 * 
	 * @param classMap
	 *            Inital class
	 * @param methodNode
	 *            Initial method
	 * @return
	 */
	private static MappedMethod getInterfaceMethod(final MappedClass classMap, final MethodNode methodNode) {
		MappedClass parentMap = classMap;
		while (parentMap != null) {
			ClassNode node = parentMap.getNode();
			for (String interfaze : node.interfaces) {
				if (rename.containsKey(interfaze)) {
					MappedClass mappedInterface = rename.get(interfaze);
					Map<String, MappedMethod> methods = mappedInterface.getMethodByName(methodNode.name);
					if (methods != null) {
						MappedMethod mm = methods.get(methodNode.desc);
						if (mm != null) {
							return mm;
						}
					}
				} else {
					return new MappedMethod(methodNode.name, methodNode.name, methodNode.desc);
				}
			}
			parentMap = parentMap.getParent();
		}

		return null;
	}

	/**
	 * Attempt to find the given method in a parent class, given the inital
	 * class the method belongs to.
	 * 
	 * @param classMap
	 *            Initial class
	 * @param methodNode
	 *            Initial method
	 * @return
	 */
	private static MappedMethod getParentBackup(final MappedClass classMap, final MethodNode methodNode) {
		try {
			ClassReader cr = new ClassReader(methodNode.owner.name);
			ClassNode clazz = ASMUtil.getNode(cr.bytes);
			if (clazz != null && clazz.superName != null) {
				cr = new ClassReader(clazz.superName);
				clazz = ASMUtil.getNode(cr.bytes);
				while (clazz != null) {
					for (MethodNode mn : clazz.methods) {
						if (mn.name.equals(methodNode.name)) {
							if (rename.containsKey(clazz.name)) {
								MappedClass mc = rename.get(clazz.name);
								Map<String, MappedMethod> methods = mc.getMethodByName(methodNode.name);
								if (methods != null) {
									MappedMethod mm = methods.get(methodNode.desc);
									if (mm != null) {
										return mm;
									}
								}
							} else {
								return new MappedMethod(methodNode.name, methodNode.name, methodNode.desc);
							}
						}
					}
					if (clazz.name.contains("java/lang/Object")) {
						clazz = null;
						break;
					} else {
						cr = new ClassReader(clazz.superName);
						clazz = ASMUtil.getNode(cr.bytes);
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Update the last mapped jar file
	 * 
	 * @param jarFile
	 */
	public static void setLast(File jarFile) {
		lastUsed = jarFile;
	}

	/**
	 * Get the last used jar file
	 * 
	 * @return
	 */
	public static File getLast() {
		return lastUsed;
	}

	/**
	 * Get the main class file (Found by mapping the jar file)
	 * 
	 * @return
	 */
	public static String getMain() {
		return mainClass;
	}

	private static boolean isMain(MethodNode methodNode) {
		if (!methodNode.name.equals("main")) {
			return false;
		}
		if (!methodNode.desc.equals("([Ljava/lang/String;)V")) {
			return false;
		}
		return true;
	}

	public static void setMain(String name) {
		mainClass = name;
	}
}
