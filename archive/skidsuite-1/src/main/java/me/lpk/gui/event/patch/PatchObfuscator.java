package me.lpk.gui.event.patch;

import java.io.File;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import me.lpk.asm.deob.DeobfuscationVisitor;
import me.lpk.asm.deob.EnumDeobfuscation;
import me.lpk.gui.Main;
import me.lpk.mapping.MappingGen;
import me.lpk.util.JarUtil;

public class PatchObfuscator implements EventHandler<ActionEvent> {
	private final EnumDeobfuscation deob;

	public PatchObfuscator(EnumDeobfuscation deob) {
		this.deob = deob;
	}

	@Override
	public void handle(ActionEvent ae) {
		try {
			File jar = Main.getTargetJar();
			Map<String, byte[]> out = JarUtil.loadNonClassEntries(jar);
			Map<String, ClassNode> nodes = JarUtil.loadClasses(jar);
			MappingGen.setLast(jar);
			System.out.println("Saving " + nodes.size() + " classes... ");
			int workIndex = 1;
			for (ClassNode cn : nodes.values()) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cn.accept(new DeobfuscationVisitor(cw, deob, cn, nodes));
				out.put(cn.name, cw.toByteArray());
				//
				String percentStr = "" + ((workIndex + 0.000000001f) / (nodes.size() - 0.00001f)) * 100;
				percentStr = percentStr.substring(0, percentStr.length() > 5 ? 5 : percentStr.length());
				System.out.println("\t" + workIndex + "/" + nodes.size() + " [" + percentStr + "%]");
				workIndex++;
			}
			JarUtil.saveAsJar(out, jar.getName().replace(".jar", "") + "_Re_" + deob.name() + ".jar", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
