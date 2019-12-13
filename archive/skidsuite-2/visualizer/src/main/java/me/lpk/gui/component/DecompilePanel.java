package me.lpk.gui.component;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.gui.component.decompilers.ASMMode;
import me.lpk.gui.component.decompilers.DecompileMode;
import me.lpk.gui.component.decompilers.ProcyonMode;
import me.lpk.util.JarUtils;
import me.lpk.util.SwingUtils;

public class DecompilePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private DecompileMode mode = new ASMMode();
	private JScrollPane scrollFiles = new JScrollPane();
	private StyledDocument doc = new DefaultStyledDocument();
	private JTextPane txtEdit = new JTextPane(doc);
	private JScrollPane scrollEdit = new JScrollPane();
	private Map<String, ClassNode> nodes;
	//private Map<String, Map<String, Integer>> methodIndecies = new HashMap<String, Map<String, Integer>>();
	//private Map<String, Map<String, Integer>> fieldIndecies = new HashMap<String, Map<String, Integer>>();
	private ClassNode currNode;
	private ActionListener jarOpenListner;
	private MouseAdapter mouseListener;

	public DecompilePanel() {
		setup(true);
	}

	public DecompilePanel(boolean supportDrag) {
		setup(supportDrag);
	}

	private void setup(boolean supportDrag) {
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(260);
		this.setLayout(new BorderLayout());
		this.add(splitPane, BorderLayout.CENTER);
		if (supportDrag) {
			this.setDropTarget(new DropTarget() {
				private static final long serialVersionUID = 1L;

				@Override
				public final void drop(final DropTargetDropEvent event) {
					try {
						event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						final Object transferData = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						if (transferData == null) {
							return;
						}
						@SuppressWarnings("unchecked")
						final Iterator<File> iterator = ((List<File>) transferData).iterator();
						while (iterator.hasNext()) {
							final File file;
							if ((file = iterator.next()).getName().endsWith("jar")) {
								openJar(new File(file.getAbsolutePath()));
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			});
		}
		scrollEdit.setViewportView(txtEdit);
		txtEdit.setEditable(false);
		splitPane.setRightComponent(scrollEdit);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(supportDrag ? "Drag and drop a file!" : "Open a file!");
		tree = new JTree(SwingUtils.sort(root));
		tree.setEditable(true);
		scrollFiles.setViewportView(tree);
		splitPane.setLeftComponent(scrollFiles);
	}

	/**
	 * Loads a jar file into the file navigator and updates the map of
	 * ClassNodes.
	 * 
	 * @param file
	 */
	public void openJar(File file) {
		// Skidded from JByteEdit
		// <3 you Quux
		// final ArrayList<JarEntry> entries = Collections.list(file.entries());
		// final Iterator<JarEntry> iterator = (Iterator<JarEntry>)
		// entries.iterator();
		final StringTreeNode root = new StringTreeNode(file.getName(), "");
		try {
			nodes = JarUtils.loadClasses(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (nodes == null) {
			return;
		}
		mode.upateFile(file);
		mode.updateNodes(nodes);
		for (ClassNode classNode : nodes.values()) {
			final ArrayList<String> dirPath = new ArrayList<String>(Arrays.asList(classNode.name.split("/")));
			StringTreeNode parent = root;
			while (dirPath.size() > 0) {
				final String section = dirPath.get(0);
				StringTreeNode node;
				if ((node = parent.getChild(section)) == null) {
					final StringTreeNode newDir = new StringTreeNode(section, classNode.name);
					parent.addChild(section, newDir);
					parent.add(newDir);
					node = newDir;
				}
				parent = node;
				dirPath.remove(0);
			}
		}
		tree = new JTree(SwingUtils.sort(root));
		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tree.getSelectionPath() != null) {
					String path = tree.getSelectionPath().toString();
					path = path.substring(1, path.length() - 1);
					while (path.contains(", ")) {
						path = path.replace(", ", "/");
					}
					path = path.substring(path.indexOf("/") + 1);
					if (nodes.containsKey(path)) {
						decompile(path);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});
		if (jarOpenListner != null) {
			jarOpenListner.actionPerformed(null);
		}
		scrollFiles.setViewportView(tree);
		scrollFiles.repaint();
	}

	/**
	 * Converts the selected path into syntax-highlighted bytecode.
	 * 
	 * @param path
	 */
	public void decompile(String path) {
		ClassNode cn = nodes.get(path);
		if (cn == null) {
			System.err.println(path + " IS NOT A CLASSNODE!");
			return;
		}
		currNode = cn;
		mode.updateCurrentNode(cn);
		mode.decompile(cn, txtEdit, doc);
		// TODO: Setup method/field indecies based on 'output'
		txtEdit.setCaretPosition(0);
	}

	/**
	 * Returns a selection object containing data about the selected text.
	 * 
	 * @return
	 */
	public DecompileSelection getSelection() {
		return mode.getSelection(txtEdit);
	}

	public void setIndex(SearchResultEntry result) {
		if (result.getMethod() != null) {
			mode.find(result, txtEdit);
		}
	}

	public void setJarListener(ActionListener listener) {
		this.jarOpenListner = listener;
	}

	public Map<String, ClassNode> getNodes() {
		return nodes;
	}

	public ClassNode getCurrentNode() {
		return currNode;
	}

	public void setNode(String className, ClassNode node) {
		nodes.put(className, node);
	}

	public void setNodes(Map<String, ClassNode> nodes) {
		this.nodes = nodes;
	}

	public MouseAdapter getMouseListener() {
		return mouseListener;
	}

	public void setMouseListener(MouseAdapter newListener) {
		MouseAdapter oldListener = getMouseListener();
		if (oldListener != null) {
			txtEdit.removeMouseListener(oldListener);
		}
		mouseListener = newListener;
		txtEdit.addMouseListener(newListener);
	}

	public JScrollPane getTextScroll() {
		return scrollEdit;
	}

	public void setMode(String string) {
		switch (string) {
		case "ASM":
			mode = new ASMMode(mode);
			break;
		case "Procyon":
			mode = new ProcyonMode(mode);
			break;
		}
		if (currNode != null){
			decompile(currNode.name);
		}
	}
}
