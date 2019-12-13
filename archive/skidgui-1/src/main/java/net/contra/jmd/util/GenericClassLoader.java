package net.contra.jmd.util;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: Dec 9, 2010
 * Time: 4:23:08 AM
 */
public class GenericClassLoader extends ClassLoader {
    public GenericClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> loadClass(String name, byte[] crap) {
        //name = name.substring(0, name.lastIndexOf('.'));
        Class c = null;
        try {
            //c = super.defineClass(crap, 0, crap.length);
            c = super.defineClass(name, crap, 0, crap.length);
        } catch (Exception e) {
            return c;
        }
        super.resolveClass(c);
        return c;
    }
}
