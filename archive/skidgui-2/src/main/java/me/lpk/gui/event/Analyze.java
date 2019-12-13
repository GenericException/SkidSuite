package me.lpk.gui.event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import me.lpk.asm.AbstractMethodTransformer;
import me.lpk.asm.threat.ClassThreat;
import me.lpk.asm.threat.JarThreat;
import me.lpk.asm.threat.ThreatClassVisitor;
import me.lpk.gui.Main;
import me.lpk.util.JarUtil;
import me.lpk.util.StringUtil;

public class Analyze implements EventHandler<ActionEvent> {
	public static final int DUMP_IP = 1, DUMP_URL = 2, SUSP_REP = 0;
	public int mode;

	public Analyze(int mode) {
		this.mode = mode;
	}

	@Override
	public void handle(ActionEvent event) {
		if (mode == SUSP_REP) {
			try {
				File jar = Main.getTargetJar();
				JarThreat jarThreat = new JarThreat(jar.getName());
				Map<String, ClassNode> nodes = JarUtil.loadClasses(jar);
				System.out.println("Analyzing " + nodes.size() + " classes... ");
				int workIndex = 1;
				List<String> ignored = new ArrayList<String>();//AnalyzeTab.getIgnored();
				for (ClassNode cn : nodes.values()) {
					boolean skip = false;
					for (String pack : ignored) {
						if (cn.name.startsWith(pack)) {
							skip = true;
						}
					}
					if (!skip) {
						ClassThreat ct = new ClassThreat(cn.name);
						ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
						cn.accept(new ThreatClassVisitor(cw, cn, ct, nodes));
						jarThreat.add(ct);
					}
					String percentStr = "" + ((workIndex + 0.000000001f) / (nodes.size() - 0.00001f)) * 100;
					percentStr = percentStr.substring(0, percentStr.length() > 5 ? 5 : percentStr.length());
					System.out.println("	" + workIndex + "/" + nodes.size() + " [" + percentStr + "%]");
					workIndex++;
				}
				String post = "-Report.html";
				FileUtils.write(new File(Main.getTargetJar().getName() + post), jarThreat.asHTML());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		List<String> lines = new ArrayList<String>();
		try {
			File jar = Main.getTargetJar();
			Map<String, ClassNode> nodes = JarUtil.loadClasses(jar);
			System.out.println("Analyzing " + nodes.size() + " classes... ");
			int workIndex = 1;
			for (ClassNode cn : nodes.values()) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cn.accept(new StringVisitor(cw, cn, lines));
				//
				String percentStr = "" + ((workIndex + 0.000000001f) / (nodes.size() - 0.00001f)) * 100;
				percentStr = percentStr.substring(0, percentStr.length() > 5 ? 5 : percentStr.length());
				System.out.println("	" + workIndex + "/" + nodes.size() + " [" + percentStr + "%]");
				workIndex++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lines.size() >= 0) {
			try {
				String post = ((mode == DUMP_IP) ? "-IPs.txt" : ((mode == DUMP_URL) ? "-URLs.txt" : ".txt"));
				FileUtils.writeLines(new File(Main.getTargetJar().getName() + post), lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class StringVisitor extends ClassVisitor {
		private final AbstractMethodTransformer trans;

		public StringVisitor(ClassVisitor cv, ClassNode node, List<String> list) {
			super(Opcodes.ASM5, cv);
			trans = new StringMethodTransformer(node, list);
		}

		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			if (mv == null) {
				return null;
			}
			for (MethodNode mn : trans.getNode().methods) {
				if (mn.name.equals(name) && mn.desc.equals(desc)) {
					trans.transform(mn);
				}
			}
			return mv;
		}

		private class StringMethodTransformer extends AbstractMethodTransformer {
			private final List<String> list;

			public StringMethodTransformer(ClassNode node, List<String> list) {
				super(node);
				this.list = list;
			}

			@Override
			public void transform(MethodNode method) {
				for (AbstractInsnNode ain : method.instructions.toArray()) {
					if (ain.getOpcode() == Opcodes.LDC) {
						LdcInsnNode lin = (LdcInsnNode) ain;
						String cst = lin.cst.toString();
						if ((mode == DUMP_IP || mode == SUSP_REP) && StringUtil.isIP(cst)) {
							list.add(method.owner.name + "." + method.name + "() found IP: " + cst);
						}
						if ((mode == DUMP_URL || mode == SUSP_REP) && StringUtil.isLink(cst)) {
							list.add(method.owner.name + "." + method.name + "() found URL: " + cst);
						}
					}
				}
			}
		}
	}
}
