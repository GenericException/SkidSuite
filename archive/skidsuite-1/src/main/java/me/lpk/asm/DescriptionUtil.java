package me.lpk.asm;

import java.util.HashMap;
import java.util.Map;

public class DescriptionUtil {
	private final String desc;
	private final Map<Integer, String> params;
	private final Class<?> clazz;

	private DescriptionUtil(String desc, Map<Integer, String> params, Class<?> clazz) {
		this.desc = desc;
		this.params = params;
		this.clazz = clazz;
	}

	/**
	 * TODO: Support for Arrays '['
	 * 
	 * @param desc
	 * @return
	 */
	public static DescriptionUtil get(String desc) {
		Map<Integer, String> paramMap = new HashMap<Integer, String>();
		int params = 0;
		Class<?> clazz = null;
		StringBuilder sb = new StringBuilder(desc.substring(1));
		StringBuilder sbTemp = new StringBuilder();
		boolean constant = true, recParams = true, returnConstant = false;
		while (sb.length() > 0) {
			String nextChar = sb.toString().substring(0, 1);
			if (!constant && !nextChar.equals(";") && !nextChar.equals(")")) {
				sbTemp.append(nextChar);
			}
			if (!returnConstant && sb.length() == 1) {
				try {
					//System.out.println(desc);
					clazz = Class.forName(sbTemp.toString().replace("/", "."));
				} catch (NoClassDefFoundError | ClassNotFoundException e) {
					//e.printStackTrace();
					clazz = null;
				}
			}
			switch (nextChar) {
			case "I":
			case "J":
			case "F":
			case "D":
			case "S":
			case "C":
				if (constant && recParams) {
					paramMap.put(params, nextChar);
					params++;
				} else if (returnConstant) {
					switch (nextChar) {
					case "I":
						clazz = int.class;
					case "J":
						clazz = long.class;
					case "F":
						clazz = float.class;
					case "D":
						clazz = double.class;
					case "S":
						clazz = String.class;
					case "C":
						clazz = Class.class;
					}
				}
				break;
			case ";":
				constant = true;
				if (recParams) {
					paramMap.put(params, sbTemp.toString());
					sbTemp = new StringBuilder();
					params++;
				}
				break;
			case ")":
				recParams = false;
				if (sb.length() == 2) {
					returnConstant = true;
				}
				sbTemp = new StringBuilder();
				break;

			case "L":
				if (constant) {
					constant = false;
				}
				break;
			}
			sb.replace(0, 1, "");
		}
		return new DescriptionUtil(desc, paramMap, clazz);
	}

	public String getDesc() {
		return desc;
	}

	public Map<Integer, String> getParams() {
		return params;
	}

	public int getParamCount() {
		return params.size();
	}

	public Class<?> getClassType() {
		return clazz;
	}
}
