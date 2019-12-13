package net.contra.jmd.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;

public final class NonClassEntries {
    public static ArrayList<JarEntry> entries = new ArrayList<JarEntry>();
    public static Map<JarEntry, InputStream> ins = new HashMap<JarEntry, InputStream>();

    private NonClassEntries() {
    }

    public static JarEntry getByName(String name) {
        for (JarEntry e : entries) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static void add(JarEntry entry, InputStream inputStream) {
        entries.add(entry);
        ins.put(entry, inputStream);
    }

}
