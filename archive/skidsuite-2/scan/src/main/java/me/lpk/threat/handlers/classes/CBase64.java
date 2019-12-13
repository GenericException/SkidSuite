package me.lpk.threat.handlers.classes;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import me.lpk.threat.handlers.ClassHandler;
import me.lpk.threat.result.ThreatResult;
import me.lpk.util.RegexUtils;

public class CBase64 extends ClassHandler {

	@Override
	public ThreatResult scanClass(ClassNode cn) {
		String regex = "^(?:[A-Za-z0-9+\\/]{4})*(?:[A-Za-z0-9+\\/]{2}==|[A-Za-z0-9+\\/]{3}=)?$";
		List<String> decrypts = new ArrayList<String>();
		for (MethodNode mn : cn.methods) {
			for (AbstractInsnNode ain : mn.instructions.toArray()) {
				if (ain.getType() != AbstractInsnNode.LDC_INSN) {
					continue;
				}
				LdcInsnNode ldc = (LdcInsnNode) ain;
				if (ldc.cst instanceof String) {
					String encoded = ldc.cst.toString();
					// TODO: Improve match regex
					// It doesn't detect if it's valid. Just if it is the right length/chars
					byte[] bytes = encoded.getBytes();
					if (encoded.contains("==") && bytes.length >= 4 && RegexUtils.isMatch(regex, encoded)){
						String decoded = new String(Base64.getDecoder().decode(bytes));
						decrypts.add(decoded);
					}
				}
			}
		}
		if (decrypts.size() > 0) {
			return ThreatResult.withData(getName(), getDesc(), decrypts);
		}
		return null;
	}

	@Override
	public String getName() {
		return "Base64 Encryption";
	}

	@Override
	public String getDesc() {
		return "The class hase Base64 encoded strings.";
	}

}
