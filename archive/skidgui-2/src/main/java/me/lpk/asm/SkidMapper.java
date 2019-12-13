package me.lpk.asm;

import java.util.Map;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;
import me.lpk.mapping.objects.MappedClass;
import me.lpk.mapping.objects.MappedField;
import me.lpk.mapping.objects.MappedMethod;
import me.lpk.util.StringUtil;

public class SkidMapper extends Remapper {
	private final Map<String, MappedClass> renamed;

	public SkidMapper(Map<String, MappedClass> renamed) {
		this.renamed = renamed;
	}

	@Override
	public String mapDesc(String desc) {
		return super.mapDesc(StringUtil.fixDesc(desc, renamed));
	}

	@Override
	public String mapType(String type) {
		if (type == null) {
			return null;
		}
		return super.mapType(StringUtil.fixDesc(type, renamed));
	}

	@Override
	public String[] mapTypes(String[] types) {
		for (int i = 0; i < types.length; i++) {
			types[i] = StringUtil.fixDesc(types[i], renamed);
		}
		return super.mapTypes(types);
	}

	@Override
	public String mapMethodDesc(String desc) {
		if ("()V".equals(desc)) {
			return desc;
		}
		return super.mapMethodDesc(StringUtil.fixDesc(desc, renamed));
	}

	@Override
	public Object mapValue(Object value) {
		if (value instanceof Type) {
			return mapType((Type) value);
		}
		if (value instanceof Handle) {
			Handle h = (Handle) value;
			return new Handle(h.getTag(), mapType(h.getOwner()), mapMethodName(h.getOwner(), h.getName(), h.getDesc()), mapMethodDesc(h.getDesc()));
		}
		return super.mapValue(value);
	}

	@Override
	public String mapSignature(String signature, boolean typeSignature) {
		if (signature == null) {
			return null;
		}
		return super.mapSignature(StringUtil.fixDesc(signature, renamed), typeSignature);
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		MappedClass mc = renamed.get(owner);
		while (mc != null) {
			MappedMethod mm = null;
			Map<String, MappedMethod> mml = mc.getMethods().get(name);
			if (mml != null) {
				mm = mml.get(desc);
				if (mm != null) {
					return super.mapMethodName(owner, mm.getRenamed(), desc);
				}
			}
			mc = mc.getParent();
		}
		return super.mapMethodName(owner, name, desc);
	}

	@Override
	public String mapInvokeDynamicMethodName(String name, String desc) {
		MappedClass mc = renamed.get(StringUtil.getMappedFromDesc(renamed, desc));
		while (mc != null) {
			MappedMethod mm = null;
			Map<String, MappedMethod> mml = mc.getMethods().get(name);
			if (mml != null) {
				mm = mml.get(desc);
				if (mm != null) {
					return super.mapInvokeDynamicMethodName(mm.getRenamed(), desc);
				}
			}
			mc = mc.getParent();
		}
		return super.mapInvokeDynamicMethodName(name, desc);
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		MappedClass mc = renamed.get(owner);
		while (mc != null) {
			MappedField mf = null;
			Map<String, MappedField> mfl = mc.getFields().get(name);
			if (mfl != null) {
				mf = mfl.get(desc);
				if (mf != null) {
					return super.mapFieldName(owner, mf.getRenamed(), desc);
				}
			}
			mc = mc.getParent();
		}
		return super.mapFieldName(owner, name, desc);
	}

	@Override
	public String map(String typeName) {
		typeName = StringUtil.fixDesc(typeName, renamed);
		return super.map(typeName);
	}
}
