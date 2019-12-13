package me.lpk.mapping.objects;

public class MappedMember extends MappedObject {
	private final String desc;

	public MappedMember(String original, String renamed, String desc) {
		super(original, renamed);
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
