package me.lpk.gui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.lpk.lang.Lang;

public class SettingsBox extends JPanel {
	private static final long serialVersionUID = 1L;
	private final Map<String,JCheckBox> checkboxes = new HashMap<String,JCheckBox>();
	private final Map<String,JTextField> textboxes = new HashMap<String,JTextField>();
	public final String title;
	private final JPanel internal = new JPanel();

	public SettingsBox(String title) {
		this.title = title;
		setFont(new Font("Tahoma", Font.BOLD, 13));
		setBorder(BorderFactory.createTitledBorder(title));
		setAlignmentX(LEFT_ALIGNMENT);
		setAlignmentY(LEFT_ALIGNMENT);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		internal.setLayout(new BoxLayout(internal, BoxLayout.Y_AXIS));
		add(internal);
		
	}
	
	public void addSetting(String setting, boolean enabled){
		JCheckBox chk = new JCheckBox(Lang.translations.get(setting), enabled);
		chk.setAlignmentX(0.0f);
		checkboxes.put(setting,chk);
		internal.add(chk);
	}
	
	public void addSetting(String setting, String defaultText){
		JPanel pnl = new JPanel();
		pnl.setAlignmentX(LEFT_ALIGNMENT);
		pnl.setAlignmentY(LEFT_ALIGNMENT);
		JLabel lbl = new JLabel(Lang.translations.get(setting));
		JTextField txt = new JTextField(defaultText);
		textboxes.put(setting,txt);
		pnl.setLayout(new BorderLayout());
		pnl.add(lbl, BorderLayout.WEST);
		pnl.add(txt, BorderLayout.EAST);
		internal.add(pnl);
	}
	
	public Set<String> getBoolSettings(){
		return checkboxes.keySet();
	}
	public Set<String> getStringSettings(){
		return textboxes.keySet();
	}
	
	public boolean getSettingValueBool(String setting){
		return checkboxes.containsKey(setting) && checkboxes.get(setting).isSelected();
	}
	
	public String getSettingValueString(String setting){
		return textboxes.containsKey(setting) ? textboxes.get(setting).getText() : "";
	}
}
