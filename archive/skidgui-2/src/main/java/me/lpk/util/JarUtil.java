package me.lpk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.tree.ClassNode;

import me.lpk.mapping.MappingGen;

public class JarUtil {
	/**
	 * Creates a map of <String(Class name), ClassNode> for a given jar file
	 * 
	 * @param jarFile
	 * @author Konloch (Bytecode Viewer)
	 * @return
	 * @throws IOException
	 */
	public static Map<String, ClassNode> loadClasses(File jarFile) throws IOException {
		Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
		ZipInputStream jis = new ZipInputStream(new FileInputStream(jarFile));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			try {
				final String name = entry.getName();
				if (name.endsWith(".class")) {
					byte[] bytes = IOUtils.toByteArray(jis);
					String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
					if (cafebabe.toLowerCase().equals("cafebabe")) {
						try {
							final ClassNode cn = ASMUtil.getNode(bytes);
							classes.put(cn.name, cn);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				jis.closeEntry();
			}
		}
		jis.close();
		return classes;
	}

	/**
	 * Creates a map of <String(entry name), byte[]> for a given jar file
	 * 
	 * 
	 * @param jarFile
	 * @return
	 * @throws IOException
	 */
	public static Map<String, byte[]> loadNonClassEntries(File jarFile) throws IOException {
		Map<String, byte[]> entries = new HashMap<String, byte[]>();
		ZipInputStream jis = new ZipInputStream(new FileInputStream(jarFile));
		ZipEntry entry;
		while ((entry = jis.getNextEntry()) != null) {
			try {
				final String name = entry.getName();
				if (!name.endsWith(".class") && !entry.isDirectory()) {
					byte[] bytes = IOUtils.toByteArray(jis);
					entries.put(name, bytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				jis.closeEntry();
			}
		}
		jis.close();
		return entries;
	}

	/**
	 * Gets the manifest file of a given jar
	 * 
	 * @param jarFile
	 * @return
	 * @throws IOException
	 */
	public static String getManifest(File jarFile) throws IOException {
		URL url = new URL("jar:file:" + jarFile.getAbsolutePath() + "!/META-INF/MANIFEST.MF");
		InputStream is = url.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s + "\n");
		}
		br.close();
		is.close();
		String text = sb.toString();
		return text;
	}

	/**
	 * Saves a map of bytes to a jar file
	 * 
	 * @param out2
	 * @param fileName
	 * @param makeMainClass
	 */
	public static void saveAsJar(Map<String, byte[]> out2, String fileName, boolean hasMeta) {
		try {
			JarOutputStream out = new JarOutputStream(new java.io.FileOutputStream(fileName));
			for (String entry : out2.keySet()) {
				String ext = entry.contains(".") ? "" : ".class";
				out.putNextEntry(new ZipEntry(entry +  ext));
				out.write(out2.get(entry));
				out.closeEntry();
			}
			if (hasMeta) {
				out.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
				out.write(getManifestBytes(MappingGen.getLast()));
				out.closeEntry();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the bytes of a manifest file within a jar file given in the
	 * parameter
	 * 
	 * @param jar
	 * @return
	 */
	private static byte[] getManifestBytes(File jar) {
		try {
			if (jar != null && jar.exists()) {
				StringBuilder sb = new StringBuilder(getManifest(jar));
				StringBuilder sb2 = new StringBuilder();
				String strMain = "Main-Class: ";
				String strPath = "Class-Path: ";
				int mainIndex = sb.indexOf(strMain);
				int pathIndex = sb.indexOf(strPath);
				sb2.append("Manifest-Version: 1.0\n");
				if (pathIndex != -1) {
					String path = sb.substring(pathIndex, mainIndex);
					sb2.append(path);

				}
				if (mainIndex != -1) {
					String main = sb.substring(mainIndex, mainIndex + strMain.length()) + MappingGen.getMain() + "\n\r";
					sb2.append(main);
				}
				// UTF-8 required in case the main class contains unicode
				// characters.
				return sb2.toString().getBytes(Charset.forName("UTF-8"));
			}
			// TODO: Get a template / BS Manifest's bytes and make it the
			// default return
			return new byte[] { 1, 2, 3, 4 };
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[] { 0 };
	}
}
