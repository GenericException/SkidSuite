package me.lpk.gui.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class MethodCellRenderer extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = 123454321L;
	private static final Color COL_NORMAL = new Color(245, 245, 255);
	private static final Color COL_SELECTED = new Color(180, 180, 200);
	//
	private static final Color COL_FRAME = new Color(220, 220, 225);
	private static final Color COL_LDC = new Color(220, 240, 220);
	private static final Color COL_METHOD = new Color(240, 220, 200);
	private static final Color COL_FIELD = new Color(220, 200, 250);
	private static final Color COL_LOCAL = new Color(230, 200, 200);
	private final MethodSimulatorPanel parent;

	public MethodCellRenderer(MethodSimulatorPanel parent) {
		setOpaque(true);
		this.parent = parent;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		String s = value.toString();
		if (parent.doColorOpcodes() && s.length() > 1) {
			setBackground(COL_NORMAL);
			if (s.startsWith("F_NEW")) {
				setBackground(COL_FRAME);
			} else if (s.startsWith("LDC")) {
				setBackground(COL_LDC);
			} else if (s.startsWith("INVOKE")) {
				setBackground(COL_METHOD);
			} else if (s.startsWith("PUTFIELD") || s.startsWith("GETFIELD") || s.startsWith("PUTSTATIC") || s.startsWith("GETSTATIC")) {
				setBackground(COL_FIELD);
			} else if (s.substring(1).startsWith("LOAD") || s.substring(1).startsWith("STORE") || s.substring(2).startsWith("LOAD")
					|| s.substring(2).startsWith("STORE")) {
				setBackground(COL_LOCAL);
			} else if (s.startsWith("INVOKE")) {
				setBackground(COL_METHOD);
			}
		}
		if (isSelected) {
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			setBackground(COL_SELECTED);
		} else {
			setBorder(BorderFactory.createEmptyBorder());
		}
		setText("  " + s);
		return this;
	}
}
