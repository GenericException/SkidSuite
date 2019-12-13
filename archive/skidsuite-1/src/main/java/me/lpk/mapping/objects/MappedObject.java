package me.lpk.mapping.objects;

public class MappedObject {

	protected final String original;
	protected String renamed;

	public MappedObject(String original, String renamed) {
		this.original = original;
		this.renamed = renamed;
	}

	public String getOriginal() {
		return original;
	}

	public String getRenamed() {
		return renamed;
	}

	public void setRenamed(String renamed) {
		this.renamed = renamed;
	}
}
