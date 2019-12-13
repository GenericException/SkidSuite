package me.lpk.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.objectweb.asm.tree.ClassNode;
import me.lpk.gui.component.DecompilePanel;
import me.lpk.gui.component.SearchResultPanel;
import me.lpk.gui.listeners.ContextMenuAdapter;
import me.lpk.gui.listeners.SearchKeyListener;
import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappingFactory;

public class VisualizerWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "SkidVisualizer";
	private static final int INIT_WIDTH = 1080, INIT_HEIGHT = 800;
	private Map<String, ClassNode> nodes = new HashMap<String, ClassNode>();
	private Map<String, MappedClass> mappings;
	private final JMenuBar menuBar;
	private final JTextField txtMenuSearch;
	private final JComboBox<String> searchType;
	private final JComboBox<String> decompileMode;
	private final DecompilePanel decompilePanel;
	private final SearchResultPanel searchResults;
	public static VisualizerWindow instance;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		new VisualizerWindow().setVisible(true);
	}

	public VisualizerWindow() {
		instance = this;
		menuBar = new JMenuBar();
		txtMenuSearch = new JTextField();
		searchType = new JComboBox<String>();
		decompileMode = new JComboBox<String>();
		decompilePanel = new DecompilePanel();
		searchResults = new SearchResultPanel();
		setup();
	}

	private void setup() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(TITLE);
		String searchingTooltip = "<html>Searching:<br><br>" + 
				"Class:  package/subpackage/ClassName<br>" + 
				"Method: MethodName or ASM Description<br>"+
				"Field:  FieldName or ASM Description<br>"+
				"LDC: String/Constants</html>";
		setSize(INIT_WIDTH, INIT_HEIGHT);
		txtMenuSearch.setToolTipText(searchingTooltip );
		searchType.setToolTipText(searchingTooltip);
		JLabel searchLbl = new JLabel(" Search:  ");
		for (EnumSearchType search : EnumSearchType.values()){
			searchType.addItem(search.getDisplayText());
		}
		decompileMode.addItem("ASM");
		decompileMode.addItem("Procyon");
		decompileMode.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	decompilePanel.setMode(decompileMode.getSelectedItem().toString());
		    }
		});
		txtMenuSearch.addKeyListener(new SearchKeyListener());
		menuBar.add(searchLbl);
		menuBar.add(txtMenuSearch);
		menuBar.add(searchType);
		menuBar.add(decompileMode);
		decompilePanel.setJarListener(new JarLoadListener());
		decompilePanel.setMouseListener(new ContextMenuAdapter());
		searchResults.setup();

		// asmPanel // searchResults
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(decompilePanel);
		splitPane.setRightComponent(searchResults);
		splitPane.setDividerLocation(INIT_WIDTH - 250);
		add(menuBar, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		
		// Hardcoding because screw doing this on my own every time:
		// decompilePanel.openJar(new File("SkidASM.jar"));
		// setState(Frame.ICONIFIED);
		// MethodSimulatorPanel.load(asmPanel.getNodes().get("me/lpk/MainWindow").methods.get(2));
	}

	/**
	 * Keeping the nodes updated when a jar is opened.
	 */
	class JarLoadListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setTitle("Loading classes...");
			nodes = decompilePanel.getNodes();
			setTitle("Generating class connections...");
			mappings = MappingFactory.mappingsFromNodes(nodes);
			setTitle(TITLE);
		}
	}

	public DecompilePanel getDecompilePanel() {
		return decompilePanel;
	}

	public SearchResultPanel getResultPanel() {
		return searchResults;
	}

	public Map<String, ClassNode> getNodes() {
		return nodes;
	}

	public Map<String, MappedClass> getMappings() {
		return mappings;
	}

	public String getSearchText() {
		return txtMenuSearch.getText();
	}

	public String getSearchType() {
		return searchType.getSelectedItem().toString();
	}

	// Like the original SkidGUI but better.
	// Better size handling w/ scroll bars.
	// More data in shrinkable panes.
	// Better handling of what is related.
	//
	// DATA SHOWN:
	// ClassName, ParentName, Interfaces, Children
	// ---- Added to display when clicked
	// Fields<Name, Type, Value(if static primitive)>
	// ---- Where referenced in other classes
	// Methods<Name, Return, Parameters>
	// ---- Where referenced in other classes
	// ---- Other objects referenced
}
