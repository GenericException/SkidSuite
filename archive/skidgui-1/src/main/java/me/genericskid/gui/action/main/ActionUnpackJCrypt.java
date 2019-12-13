package me.genericskid.gui.action.main;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.security.Key;
import java.util.zip.ZipEntry;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.jar.JarInputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.swing.JOptionPane;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.net.URLClassLoader;
import java.net.URL;
import java.awt.event.ActionEvent;
import me.genericskid.gui.frames.FrameMain;
import java.awt.event.ActionListener;

public class ActionUnpackJCrypt implements ActionListener
{

    public ActionUnpackJCrypt(final FrameMain instance) { }
    
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        final File file = FrameMain.getFileObfu();
        if (file != null && file.exists()) {
            try {
                final URLClassLoader child = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
                final ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
                for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if (!entry.isDirectory()) {
                        if (entry.getName().endsWith(".class")) {
                            String className = entry.getName().replace('/', '.');
                            className = className.substring(0, className.length() - ".class".length());
                            Class<?> classToLoad = null;
                            try {
                                classToLoad = child.loadClass(className);
                            }
                            catch (Exception ex) {}
                            if (classToLoad != null) {
                                final ZipFile infectedZip = new ZipFile(file);
                                final ZipEntry e = infectedZip.getEntry("jar.dat");
                                if (e == null) {
                                    JOptionPane.showMessageDialog(null, "'" + file.getName() + "/jar.dat' could not be found.");
                                    infectedZip.close();
                                    zip.close();
                                    return;
                                }
                                final byte[] extra = e.getExtra();
                                final byte[] key = new byte[16];
                                System.arraycopy(extra, 0, key, 0, 16);
                                final byte[] iv = new byte[16];
                                System.arraycopy(extra, 16, iv, 0, 16);
                                final byte[] bMainClass = new byte[extra.length - 33];
                                System.arraycopy(extra, 33, bMainClass, 0, extra.length - 33);
                                infectedZip.close();
                                InputStream resource = null;
                                try {
                                    resource = classToLoad.getResourceAsStream("/jar.dat");
                                }
                                catch (NullPointerException ex2) {}
                                if (resource == null) {
                                    JOptionPane.showMessageDialog(null, "'" + file.getName() + "/jar.dat' could not be read.");
                                    return;
                                }
                                final Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
                                final Key sks = new SecretKeySpec(key, "AES");
                                cipher.init(2, sks, new IvParameterSpec(iv));
                                final JarInputStream jarIn = new JarInputStream(new CipherInputStream(resource, cipher));
                                final JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(new File(file.getName() + "-JCrypt.Dump.jar")));
                                copyAndWrite(jarIn, jarOut);
                                jarOut.close();
                                return;
                            }
                        }
                    }
                }
                zip.close();
            }
            catch (Exception e2) {
                e2.printStackTrace();
                JOptionPane.showMessageDialog(null, "Please send GenericSkid a copy of whatever the fuck you tried to decompile.\n\n" + e2.toString());
            }
        }
    }
    
    private static void copyAndWrite(final JarInputStream in, final JarOutputStream out) throws IOException {
        final byte[] buffer = new byte[4096];
        JarEntry inEntry;
        while ((inEntry = (JarEntry)in.getNextEntry()) != null) {
            if (inEntry.getMethod() == 0) {
                out.putNextEntry(new JarEntry(inEntry));
            }
            else {
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
