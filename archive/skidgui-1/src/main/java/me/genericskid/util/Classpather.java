package me.genericskid.util;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.io.IOException;
import java.io.File;
import java.net.URL;

public class Classpather
{
    private static final Class<?>[] parameters;
    
    static {
        parameters = new Class[] { URL.class };
    }
    
    public static void addFile(final String s) throws IOException {
        final File f = new File(s);
        addFile(f);
    }
    
    public static void addFile(final File f) throws IOException {
        addURL(f.toURL());
    }
    
    public static void addURL(final URL u) throws IOException {
        final URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        final Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            final Method method = sysclass.getDeclaredMethod("addURL", Classpather.parameters);
            method.setAccessible(true);
            method.invoke(sysloader, u);
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
}
