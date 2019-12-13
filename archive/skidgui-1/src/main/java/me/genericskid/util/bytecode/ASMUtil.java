package me.genericskid.util.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import org.objectweb.asm.tree.ClassNode;
import java.util.ArrayList;
import java.io.File;

public class ASMUtil
{
    public static ArrayList<ClassNode> loadClasses(final File jarFile) throws IOException {
        final ArrayList<ClassNode> classes = new ArrayList<>();
        final ZipInputStream jis = new ZipInputStream(new FileInputStream(jarFile));
        ZipEntry entry;
        while ((entry = jis.getNextEntry()) != null) {
            try {
                final String name = entry.getName();
                if (name.endsWith(".class")) {
                    final byte[] bytes = getBytes(jis);
                    final String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
                    if (cafebabe.toLowerCase().equals("cafebabe")) {
                        try {
                            final ClassNode cn = getNode(bytes);
                            classes.add(cn);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
                continue;
            }
            finally {
                jis.closeEntry();
            }
            jis.closeEntry();
        }
        jis.close();
        return classes;
    }
    
    public static byte[] getBytes(final InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int a = 0;
        while ((a = is.read(buffer)) != -1) {
            baos.write(buffer, 0, a);
        }
        baos.close();
        buffer = null;
        return baos.toByteArray();
    }
    
    public static ClassNode getNode(final byte[] bytez) {
        ClassReader cr = new ClassReader(bytez);
        final ClassNode cn = new ClassNode();
        try {
            cr.accept(cn, 8);
        }
        catch (Exception e3) {
            try {
                cr.accept(cn, 4);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        cr = null;
        return cn;
    }
}
