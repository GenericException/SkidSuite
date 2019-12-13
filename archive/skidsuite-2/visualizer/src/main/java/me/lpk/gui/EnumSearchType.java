package me.lpk.gui;

/**
 * Enum for the type of search used in SkidVisualizer's search bar.
 */
public enum EnumSearchType {
	LDC("String constant"), CLASS_NAME("Class name"), CLASS_CHILDREN("Classes extending"), CLASS_REF("Class reference"), METHOD_NAME("Method name"), METHOD_DESC("Method desc"), FIELD_NAME(
			"Field name"), FIELD_DESC("Field desc");

	private final String text;

	EnumSearchType(String text) {
		this.text = text;
	}

	/**
	 * Returns the text to be used for display in the search options.
	 * 
	 * @return
	 */
	public String getDisplayText() {
		return text;
	}

	/**
	 * Gets the EnumSearchType based off of display option text <i>(See
	 * {@link #getDisplayText()} )</i>.
	 * 
	 * @param searchType
	 * @return EnumSearchType by given searchType. LDC by default.
	 */
	public static EnumSearchType byDisplayText(String searchType) {
		for (EnumSearchType type : values()) {
			if (type.getDisplayText().equals(searchType)) {
				return type;
			}
		}
		return LDC;
	}
}
