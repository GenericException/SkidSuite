package me.lpk.gui.event.patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Key;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import me.lpk.gui.Main;

public class PatchJCrypt implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent ev) {
		dumpJCrypt();
	}

	/**
	 * TODO: Scrap this entirely. Should be done VIA ASM instruction reading or
	 * changing
	 * 
	 * ASM Reading: Parse the dumping technique via ASM to get the important key
	 * values. Keep the parts that can't be done via ASM (System.arrayCoppy,
	 * copyAndWrite, etc.).
	 * 
	 * 
	 * This was the code from an actual JCrypt stub decompiled and modified to
	 * dump the jar.
	 */
	private void dumpJCrypt() {
		File file = Main.getTargetJar();
		try {
			URLClassLoader child = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
			ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				if (entry.isDirectory()) {
					continue;
				}
				if (!entry.getName().endsWith(".class")) {
					continue;
				}
				String className = entry.getName().replace('/', '.');
				className = className.substring(0, className.length() - ".class".length());
				Class<?> classToLoad = null;
				try {
					classToLoad = child.loadClass(className);
				} catch (Exception ex) {
				}
				if (classToLoad == null) {
					continue;
				}
				ZipFile infectedZip = new ZipFile(file);
				ZipEntry e = infectedZip.getEntry("jar.dat");
				if (e == null) {
					JOptionPane.showMessageDialog(null, "'" + file.getName() + "/jar.dat' could not be found.");
					infectedZip.close();
					zip.close();
					return;
				}
				byte[] extra = e.getExtra();
				byte[] key = new byte[16];
				System.arraycopy(extra, 0, key, 0, 16);
				byte[] iv = new byte[16];
				System.arraycopy(extra, 16, iv, 0, 16);
				byte[] bMainClass = new byte[extra.length - 33];
				System.arraycopy(extra, 33, bMainClass, 0, extra.length - 33);
				infectedZip.close();
				InputStream resource = null;
				try {
					resource = classToLoad.getResourceAsStream("/jar.dat");
				} catch (NullPointerException ex2) {
				}
				if (resource == null) {
					JOptionPane.showMessageDialog(null, "'" + file.getName() + "/jar.dat' could not be read.");
					return;
				}
				Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
				Key sks = new SecretKeySpec(key, "AES");
				cipher.init(2, sks, new IvParameterSpec(iv));
				JarInputStream jarIn = new JarInputStream(new CipherInputStream(resource, cipher));
				JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(new File(String.valueOf(file.getName()) + "_Re_JCry.jar")));
				copyAndWrite(jarIn, jarOut);
				jarOut.close();
			}
			zip.close();
		} catch (Exception e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error unpacking stub. Are you sure this using JCrypt? If so send a copy to the the devs.\n\n" + e2.toString());
		}
	}

	private static void copyAndWrite(JarInputStream in, JarOutputStream out) throws IOException {
		byte[] buffer = new byte[4096];
		JarEntry inEntry;
		while ((inEntry = (JarEntry) in.getNextEntry()) != null) {
			if (inEntry.getMethod() == 0) {
				out.putNextEntry(new JarEntry(inEntry));
			} else {
				out.putNextEntry(new JarEntry(inEntry.getName()));
			}
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.flush();
		}
	}
}
