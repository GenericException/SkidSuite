package me.lpk.util;

import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;

/**
 * Adds jar files to the classpath.
 * <hr>
 * This is still required to let the MappingGen get ClassNodes based off of
 * classnames. But that's a sacrifice I'm OK with.
 */
public class Classpather {
	private static final Class<?>[] parameters = new Class[] { URL.class };

	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	@SuppressWarnings("deprecation")
	public static void addFile(File f) throws IOException {
		addURL(f.toURL());
	}

	public static void addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}
}