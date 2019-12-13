package me.lpk.gui;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.tree.ClassNode;

import me.lpk.threat.ThreatScanner;
import me.lpk.threat.handlers.ClassHandler;
import me.lpk.threat.handlers.MethodHandler;
import me.lpk.threat.handlers.classes.CBase64;
import me.lpk.threat.handlers.classes.CClassLoader;
import me.lpk.threat.handlers.classes.CSuspiciousSynth;
import me.lpk.threat.handlers.classes.CWinRegHandler;
import me.lpk.threat.handlers.methods.MClassLoader;
import me.lpk.threat.handlers.methods.MFileIO;
import me.lpk.threat.handlers.methods.MNativeInterface;
import me.lpk.threat.handlers.methods.MNetworkRef;
import me.lpk.threat.handlers.methods.MRuntime;
import me.lpk.threat.handlers.methods.MWebcam;
import me.lpk.util.JarUtils;
import me.lpk.util.SwingUtils;

import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import java.awt.Color;

public class Scanner {
	private final ThreatScanner th = new ThreatScanner();
	private JFrame frame;
	private JTree treeFiles;
	private JTextPane txtpntesttitle;
	private String path, jarName, text;
	private JMenuItem mnSave;
	private JCheckBox chckbxIncludeCss, chckbxAutomaticallyExportScans;
	private final Map<String, ClassHandler> classHandlers = new LinkedHashMap<String, ClassHandler>();
	private final Map<String, MethodHandler> methodHandlers = new LinkedHashMap<String, MethodHandler>();
	private final Map<String, JCheckBox> classHandlerStatus = new HashMap<String, JCheckBox>();
	private final Map<String, JCheckBox> methodHandlerStatus = new HashMap<String, JCheckBox>();

	/**
	 * Entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		new Scanner();
	}

	/**
	 * Create the application.
	 */
	public Scanner() {
		// Register handlers so the GUI can auto-load them.
		registerClassHandler(new CBase64());
		registerClassHandler(new CClassLoader());
		registerClassHandler(new CSuspiciousSynth());
		registerClassHandler(new CWinRegHandler());
		registerMethodHandler(new MClassLoader());
		registerMethodHandler(new MFileIO());
		registerMethodHandler(new MNativeInterface());
		registerMethodHandler(new MNetworkRef());
		registerMethodHandler(new MRuntime());
		registerMethodHandler(new MWebcam());
		//
		initialize();
		frame.setVisible(true);
		updateTree();

	}

	private void registerMethodHandler(MethodHandler handler) {
		String s = handler.getName();
		methodHandlers.put(s, handler);
	}

	private void registerClassHandler(ClassHandler handler) {
		String s = handler.getName();
		classHandlers.put(s, handler);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("SkidScan");
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JSplitPane splitPane = new JSplitPane();

		// Right side (Display)
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		splitPane.setDividerLocation(150);
		txtpntesttitle = new JTextPane();
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		txtpntesttitle.setContentType("text/html");
		txtpntesttitle.setEditable(false);
		scrollPane.setViewportView(txtpntesttitle);
		scrollPane.setColumnHeaderView(menuBar);
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		// Menu
		// TODO: Upload options
		mnSave = new JMenuItem("Save Report");
		mnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		mnSave.setEnabled(false);
		mnFile.add(mnSave);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		chckbxAutomaticallyExportScans = new JCheckBox("Automatically Export Scans");
		chckbxAutomaticallyExportScans.setBackground(Color.WHITE);
		mnSettings.add(chckbxAutomaticallyExportScans);

		chckbxIncludeCss = new JCheckBox("Include CSS");
		chckbxIncludeCss.setBackground(Color.WHITE);
		chckbxIncludeCss.setSelected(true);
		mnSettings.add(chckbxIncludeCss);

		// Auto-populate detection options
		JMenu mnDetectionsClass = new JMenu("Detections: Class");
		JMenu mnDetectionsMethod = new JMenu("Detections: Method");
		for (String s : classHandlers.keySet()) {
			JCheckBox chk = new JCheckBox(s, true);
			chk.setBackground(Color.WHITE);
			classHandlerStatus.put(s, chk);
			mnDetectionsClass.add(chk);
		}
		for (String s : methodHandlers.keySet()) {
			JCheckBox chk = new JCheckBox(s, true);
			chk.setBackground(Color.WHITE);
			methodHandlerStatus.put(s, chk);
			mnDetectionsMethod.add(chk);
		}
		mnSettings.add(mnDetectionsClass);
		mnSettings.add(mnDetectionsMethod);

		//
		//
		// Left side (JTree)
		JPanel pnlTree = new JPanel();
		treeFiles = new JTree(new DefaultMutableTreeNode("Loading..."));
		treeFiles.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeFiles.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				// Update the path
				path = event.getPath().toString();
				if (path.toLowerCase().endsWith(".jar]")) {
					path = path.substring(path.indexOf(", ") + 2, path.length() - 1);
					path = path.replace(", ", File.separator);
				}
			}
		});
		treeFiles.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Scan clicked elements
				File file = new File(path);
				if (file.exists()) {
					scan(file);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		splitPane.setLeftComponent(pnlTree);
		pnlTree.setLayout(new BorderLayout(0, 0));
		pnlTree.add(treeFiles, BorderLayout.CENTER);

	}

	private void updateTree() {
		// Threaded so the program can start up even in instances where there
		// are LOTS of jars to be added.
		new Thread() {
			@Override
			public void run() {
				File dir = new File(System.getProperty("user.dir"));
				treeFiles.setModel(new DefaultTreeModel(SwingUtils.sort(getTreeFromDir(dir))));
			}

		}.start();
	}

	/**
	 * Scan the file.
	 * 
	 * @param file
	 */
	private void scan(File file) {
		Map<String, ClassNode> nodes = null;
		th.reset();
		for (String s : classHandlers.keySet()) {
			if (classHandlerStatus.get(s).isSelected()) {
				th.registerClassHandler(classHandlers.get(s));
			}
		}
		for (String s : methodHandlers.keySet()) {
			if (methodHandlerStatus.get(s).isSelected()) {
				th.registerMethodHandler(methodHandlers.get(s));
			}
		}
		try {
			nodes = JarUtils.loadClasses(file);
		} catch (IOException e1) {
			txtpntesttitle.setText("<html><body><span style=\" color: red;  \">" + e1.toString() + "</span></body></html>");
		}
		for (ClassNode cn : nodes.values()) {
			th.scan(cn);
		}
		jarName = file.getName().substring(0, file.getName().length() - 4);
		text = th.toHTML(file.getName().substring(0, file.getName().indexOf(".")), chckbxIncludeCss.isSelected());
		txtpntesttitle.setText(text);
		new Thread() {
			// Instantly setting the caret position doesn't work, so delaying it
			// 50 ms is a fair work-around.
			@Override
			public void run() {
				try {
					sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				txtpntesttitle.setCaretPosition(0);
			}
		}.start();
		mnSave.setEnabled(true);
		if (chckbxAutomaticallyExportScans.isSelected()) {
			save();
		}
	}

	/**
	 * Saves the scan to the filesystem.
	 */
	private void save() {
		try {
			FileUtils.write(new File(jarName + "-Scan.html"), text);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error saving file!", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Recursive generation of the JTree file display.
	 * 
	 * @param dir
	 * @return
	 */
	private DefaultMutableTreeNode getTreeFromDir(File dir) {
		// From a directory create a node
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(dir.getName());
		for (File file : dir.listFiles()) {
			// Iterate children
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
			if (file.isDirectory()) {
				// Recurse through the sub-directory
				DefaultMutableTreeNode subdir = getTreeFromDir(file);
				if (nodeModelContains(subdir, ".jar"))
					// Only add the node if the subdirectory contains jars.
					top.add(subdir);
			} else {
				// Add the file node if it's a jar
				if (file.getName().toLowerCase().endsWith(".jar")) {
					top.add(node);
				}
			}
		}
		return top;
	}

	/**
	 * Checks if a node's model contains the given text.
	 * 
	 * @param subdir
	 * @param text
	 * @return
	 */
	private boolean nodeModelContains(DefaultMutableTreeNode node, String text) {
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = node.children();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode dmtn = e.nextElement();
			if (dmtn.isLeaf() && dmtn.toString().toLowerCase().contains(text)) {
				return true;
			}
		}
		return false;
	}

}
