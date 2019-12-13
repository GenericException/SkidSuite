package me.lpk.gui.component;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import me.lpk.analysis.StackFrame;
import me.lpk.analysis.StackUtil;
import me.lpk.util.OpUtils;

public class MethodSimulatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JList<String> listOpcodes = new JList<String>();
	private final JTextArea txtBefore = new JTextArea();
	private final JTextArea txtAfter = new JTextArea();
	private InsnList insns;
	private StackFrame[] frames;

	public MethodSimulatorPanel() {
		setup();
		// TODO: Click on the list of opcodes instead of having to click
		// next/prev
		//
		// TODO: Right click on certain opcodes to navigate to the member
		// (MethodCalls)
		// Opens in a new window?
	}

	private void setup() {
		setLayout(new BorderLayout());
		JPanel panelDisplay = new JPanel();
		add(panelDisplay);
		panelDisplay.setLayout(new BorderLayout(0, 0));
		JSplitPane splitPaneDisplay = new JSplitPane();
		splitPaneDisplay.setOrientation(JSplitPane.VERTICAL_SPLIT);
		JScrollPane scrollOpcodes = new JScrollPane();

		panelDisplay.add(splitPaneDisplay);
		scrollOpcodes.setViewportView(listOpcodes);
		listOpcodes.setCellRenderer(new MethodCellRenderer(this));
		splitPaneDisplay.setLeftComponent(scrollOpcodes);

		JSplitPane splitBeforeAfter = new JSplitPane();
		splitPaneDisplay.setRightComponent(splitBeforeAfter);
		JScrollPane scrollBefore = new JScrollPane();
		JScrollPane scrollAfter = new JScrollPane();
		splitBeforeAfter.setLeftComponent(scrollBefore);
		splitBeforeAfter.setRightComponent(scrollAfter);
		txtBefore.setText("Before                                           ");
		txtAfter.setText("After");
		scrollBefore.setViewportView(txtBefore);
		scrollAfter.setViewportView(txtAfter);

		listOpcodes.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// if (!e.getValueIsAdjusting()){
				int indexBefore = listOpcodes.getSelectedIndex();
				if (indexBefore == -1) {
					indexBefore = 0;
				}
				int indexAfter = indexBefore + 1;
				if (indexAfter >= frames.length - 1) {
					indexAfter = frames.length - 2;
				}
				if (indexBefore >= frames.length - 1) {
					indexBefore = frames.length - 2;
				}
				StackFrame sfBefore = frames[indexBefore];
				StackFrame sfAfter = frames[indexAfter];
				String beforeText = "", afterText = "";
				if (sfBefore == null) {
					beforeText = "ERROR RESOLVING STACK @" + indexBefore;
				} else {
					beforeText = sfBefore.toString();
				}
				if (sfAfter == null) {
					afterText = "ERROR RESOLVING STACK @" + indexAfter;
				} else {
					afterText = sfAfter.toString();
				}
				txtBefore.setText("Current stack at @" + indexBefore + ":\n\n" + beforeText);
				txtAfter.setText("Stack after @" + indexBefore + ":\n\n" + afterText);
			}

		});
	}

	public static void load(ClassNode owner, MethodNode mn) {
		MethodSimulatorPanel msp = new MethodSimulatorPanel();
		// Setting up opcodes
		List<String> opcodesText = new ArrayList<String>();
		msp.insns = mn.instructions;
		for (AbstractInsnNode ain : msp.insns.toArray()) {
			opcodesText.add(toText(ain));
		}
		msp.listOpcodes.setListData(opcodesText.toArray(new String[0]));
		// Setting up Stack
		msp.frames = StackUtil.getFrames(owner.name, mn);
		// Making the frame
		JFrame frame = new JFrame();
		frame.setSize(1000, 555);
		frame.setContentPane(msp);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(owner.name + "." + mn.name + mn.desc);
	}

	private static String toText(AbstractInsnNode ain) {
		String base = OpUtils.getOpcodeText(ain.getOpcode());
		switch (ain.getType()) {
		case AbstractInsnNode.FIELD_INSN:
			FieldInsnNode fin = (FieldInsnNode) ain;
			return base + " " + fin.owner + "." + fin.name + " " + fin.desc;
		case AbstractInsnNode.IINC_INSN:
			IincInsnNode iin = (IincInsnNode) ain;
			return base + "[local " + iin.var + "] +" + iin.incr;
		case AbstractInsnNode.INSN:
			if (ain.getOpcode() >= Opcodes.ICONST_0 && ain.getOpcode() <= Opcodes.ICONST_M1) {
				return base + " " + OpUtils.getIntValue(ain);
			}
			return base;
		case AbstractInsnNode.INT_INSN:
			return base + " " + OpUtils.getIntValue(ain);
		case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
			InvokeDynamicInsnNode idin = (InvokeDynamicInsnNode) ain;
			return base + idin.name + " " + idin.desc;
		case AbstractInsnNode.JUMP_INSN:
			JumpInsnNode jin = (JumpInsnNode) ain;
			return base + " Offset: " + setIgnoreFlags(jin.label.getLabel()).getOffset();
		case AbstractInsnNode.LABEL:
			LabelNode lin = (LabelNode) ain;
			return base + " Offset: " + setIgnoreFlags(lin.getLabel()).getOffset();
		case AbstractInsnNode.LDC_INSN:
			LdcInsnNode ldc = (LdcInsnNode) ain;
			if (ldc.cst instanceof Type) {
				Type type = (Type) ldc.cst;
				return base + " Type:" + type.toString();
			}
			return base + " " + ldc.cst.toString();
		case AbstractInsnNode.LOOKUPSWITCH_INSN:
			LookupSwitchInsnNode asin = (LookupSwitchInsnNode) ain;
			String s = "";
			for (LabelNode o : asin.labels) {
				s += setIgnoreFlags(o.getLabel()).getOffset() + ",";
			}
			if (s.contains(",")) {
				s = s.substring(0, s.lastIndexOf(","));
			}
			return base + " Start: " + setIgnoreFlags(asin.dflt.getLabel()).getOffset() + " Endpoints[" + s + "]";
		case AbstractInsnNode.METHOD_INSN:
			MethodInsnNode min = (MethodInsnNode) ain;
			return base + " " + min.owner + "." + min.name + min.desc;
		case AbstractInsnNode.MULTIANEWARRAY_INSN:
			MultiANewArrayInsnNode main = (MultiANewArrayInsnNode) ain;
			return base + " " + main.desc + "[" + main.dims + "]";
		case AbstractInsnNode.TABLESWITCH_INSN:
			TableSwitchInsnNode tsin = (TableSwitchInsnNode) ain;
			String s2 = "";
			for (LabelNode o : tsin.labels) {
				s2 += setIgnoreFlags(o.getLabel()).getOffset() + ",";
			}
			if (s2.contains(",")) {
				s2 = s2.substring(0, s2.lastIndexOf(","));
			}

			return base + " min/max: " + tsin.min + "/" + tsin.max + " Endpoints[" + s2 + "]";
		case AbstractInsnNode.TYPE_INSN:
			TypeInsnNode tin = (TypeInsnNode) ain;
			return base + " " + tin.desc;
		case AbstractInsnNode.VAR_INSN:
			VarInsnNode var = (VarInsnNode) ain;
			if (var.var == 0) {
				return base + " local[" + var.var + "] //'this'";
			}
			return base + " local[" + var.var + "]";
		}
		return base;
	}

	private static Label setIgnoreFlags(Label label) {
		// status & RESOLVED
		try {
			Field field = Label.class.getDeclaredField("flags");
			field.setAccessible(true);
			field.set(label, 4);
		} catch(Exception ex) {}
		return label;
	}

	public boolean doColorOpcodes() {
		return true;
	}
}
