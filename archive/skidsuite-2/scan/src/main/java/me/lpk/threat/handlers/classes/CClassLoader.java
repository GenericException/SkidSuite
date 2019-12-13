package me.lpk.threat.handlers.classes;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import me.lpk.threat.handlers.ClassHandler;

import me.lpk.threat.result.ThreatResult;

public class CClassLoader extends ClassHandler {

	@Override
	public ThreatResult scanClass(ClassNode cn) {
		List<String> fields = new ArrayList<String>();
		if (cn.superName.contains("ClassLoader")) {
			return new ThreatResult(getName(),getDesc() , cn.name);
		}
		// Scan fields for classloaders.
		for (FieldNode fn : cn.fields) {
			if (fn == null) {
				continue;
			}
			if (fn.desc.contains("ClassLoader")) {
				fields.add(fn.name + "-" + fn.desc);
			}
		}
		if (fields.size() == 0) {
			return null;
		}
		String out = "";
		for (String field : fields) {
			out += field + ", ";
		}
		return new ThreatResult(getName(), "This class has fields that allow loading new classes at runtime.", cn.name + ":<br>Fields: " + out);
	}

	@Override
	public String getName() {
		return "Extended ClassLoader";
	}
	
	@Override
	public String getDesc() {
		return "This class allows loading new classes at runtime.";
	}

}
