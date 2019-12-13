package me.lpk.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/samczsun">samczsun</a>
 */
public class PrimitiveUtils {
	private static final Map<String, Class<?>> nameToPrimitive = new HashMap<>();
	private static final Map<Class<?>, Object> defaultPrimitiveValues = new HashMap<>();

	static {
		defaultPrimitiveValues.put(Integer.TYPE, 0);
		defaultPrimitiveValues.put(Long.TYPE, 0L);
		defaultPrimitiveValues.put(Double.TYPE, 0D);
		defaultPrimitiveValues.put(Float.TYPE, 0F);
		defaultPrimitiveValues.put(Boolean.TYPE, false);
		defaultPrimitiveValues.put(Character.TYPE, '\0');
		defaultPrimitiveValues.put(Byte.TYPE, (byte) 0);
		defaultPrimitiveValues.put(Short.TYPE, (short) 0);
		defaultPrimitiveValues.put(Object.class, null);
		nameToPrimitive.put("int", Integer.TYPE);
		nameToPrimitive.put("long", Long.TYPE);
		nameToPrimitive.put("double", Double.TYPE);
		nameToPrimitive.put("float", Float.TYPE);
		nameToPrimitive.put("boolean", Boolean.TYPE);
		nameToPrimitive.put("char", Character.TYPE);
		nameToPrimitive.put("byte", Byte.TYPE);
		nameToPrimitive.put("short", Short.TYPE);
		nameToPrimitive.put("void", Void.TYPE);
	}

	@SuppressWarnings("unchecked")
	public static <T> T castToPrimitive(Object start, Class<T> prim) {
		try {
			if (start == null) {
				return (T) defaultPrimitiveValues.get(prim);
			}
			if (start instanceof Boolean) {
				start = ((Boolean) start) ? 1 : 0;
			}
			if (start instanceof Character) {
				start = (int) ((Character) start).charValue();
			}
			if (prim == char.class) {
				return (T) Character.valueOf((char) ((Number) start).intValue());
			}
			switch (prim.getName()) {
			case "int":
				return (T) (Object) ((Number) start).intValue();
			case "long":
				return (T) (Object) ((Number) start).longValue();
			case "short":
				return (T) (Object) ((Number) start).shortValue();
			case "double":
				return (T) (Object) ((Number) start).doubleValue();
			case "float":
				return (T) (Object) ((Number) start).floatValue();
			case "byte":
				return (T) (Object) ((Number) start).byteValue();
			case "boolean":
				return (T) (Object) (((Number) start).intValue() != 0 ? true : false);
			default:
				throw new IllegalArgumentException(prim.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) start;
	}

	public static Class<?> getPrimitiveByName(String name) {
		return nameToPrimitive.get(name);
	}

	public static Object getDefaultValue(Class<?> primitive) {
		return defaultPrimitiveValues.get(primitive);
	}
}