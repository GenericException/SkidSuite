package me.lpk.util;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {
	/**
	 * TODO: Using ASM copy out the method into an empty class. Then use
	 * reflection on the empty class. Will possibly prevent malicious code from
	 * executing in <clinit>
	 * 
	 * @param owner
	 * @param name
	 * @param in
	 * @return
	 */
	@Deprecated
	public static Object getValue(String owner, String name, Class<?> param, Object... args) {
		try {
			return Class.forName(owner.replace("/", ".")).getDeclaredMethod(name, param, int.class).invoke(null, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return "FAILED_GET_VALUE";
	}
}
