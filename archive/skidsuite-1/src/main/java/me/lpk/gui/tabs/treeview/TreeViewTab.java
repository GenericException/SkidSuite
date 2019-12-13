package me.lpk.gui.tabs.treeview;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.tree.ClassNode;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import me.lpk.gui.Main;
import me.lpk.gui.tabs.BasicTab;
import me.lpk.mapping.MappingGen;
import me.lpk.mapping.modes.ModeNone;
import me.lpk.mapping.objects.MappedClass;
import me.lpk.util.AlphabeticalComparator;
import me.lpk.util.JarUtil;

public abstract class TreeViewTab extends BasicTab {
	protected final Map<String, ClassNode> nodes = new TreeMap<String, ClassNode>(new AlphabeticalComparator());
	protected final Map<String, MappedClass> remap = new TreeMap<String, MappedClass>(new AlphabeticalComparator());
	protected TreeView<String> tree;

	@Override
	protected BorderPane createOtherStuff() {
		TreeItem<String> root = new TreeItem<String>("Load a jar");
		root.setExpanded(true);
		TreeItem<String> item = new TreeItem<String>("To see its contents");
		root.getChildren().add(item);
		tree = new TreeView<String>(root);
		//
		return create(tree);
	}
	
	public abstract EventHandler<MouseEvent> getClickEvent();
	

	@Override
	public void targetLoaded() {
		tree = new TreeView<String>(createTree(true));
		tree.setOnMouseClicked(getClickEvent());
		otherControls = create(tree);
		try {
			nodes.clear();
			nodes.putAll(JarUtil.loadClasses(Main.getTargetJar()));
			remap.clear();
			remap.putAll(MappingGen.getRename(new ModeNone(), nodes));
			// Regenerate the tree again since that's all this method really
			// does. Makes it sorted.
			goBack();
		} catch (IOException e) {
			e.printStackTrace();
		}
		update();
	}

	/**
	 * Generate's a tree root.
	 * 
	 * @param fromFile
	 *            If the root should be loaded from the main jar or from
	 *            mappings.
	 * @return
	 */
	private TreeItem<String> createTree(boolean fromFile) {
		TreeItem<String> root = new TreeItem<String>(Main.getTargetJar().getName());
		try {
			if (fromFile) {
				populateFromFile(root, new ZipFile(Main.getTargetJar()));
			} else {
				populateFromMaps(root);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * Populates the given root parameter with class names based on loaded
	 * mappings.
	 * 
	 * @param root
	 */
	private void populateFromMaps(TreeItem<String> root) {
		root.getChildren().clear();
		for (String className : remap.keySet()) {
			MappedClass mc = remap.get(className);
			List<String> pathElements = Arrays.asList(className.replace(mc.getOriginal(), mc.getRenamed()).split("/"));
			TreeItem<String> parent = root;
			for (int i = 0; i < pathElements.size() - 1; i++) {
				String matchingName = pathElements.get(i);
				TreeItem<String> current = parent;
				int index = 0;
				boolean found = false;
				for (TreeItem<String> tee : current.getChildren()) {
					if (tee.getValue().equals(matchingName)) {
						found = true;
						break;
					}
					index++;
				}
				if (!found) {
					TreeItem<String> curr = new TreeItem<>(matchingName);
					current.getChildren().add(curr);
				}
				parent = current.getChildren().get(index);

			}
			parent.getChildren().add(new TreeItem<>(pathElements.get(pathElements.size() - 1)));
		}

	}

	/**
	 * Populates the given root parameter with classes within a given jar file.
	 * 
	 * @param root
	 * @param file
	 */
	private void populateFromFile(TreeItem<String> root, ZipFile file) {
		root.getChildren().clear();
		Enumeration<? extends ZipEntry> er = file.entries();
		while (er.hasMoreElements()) {
			ZipEntry entry = er.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			if (!entry.getName().endsWith(".class")) {
				continue;
			}
			List<String> pathElements = Arrays.asList(entry.getName().replace(".class", "").split("/"));
			TreeItem<String> parent = root;
			for (int i = 0; i < pathElements.size() - 1; i++) {
				String matchingName = pathElements.get(i);
				TreeItem<String> current = parent;
				int index = 0;
				boolean found = false;
				for (TreeItem<String> tee : current.getChildren()) {
					if (tee.getValue().equals(matchingName)) {
						found = true;
						break;
					}
					index++;
				}
				if (!found) {
					TreeItem<String> curr = new TreeItem<>(matchingName);
					current.getChildren().add(curr);
				}
				parent = current.getChildren().get(index);

			}
			if (entry.isDirectory()) {
				continue;
			}
			parent.getChildren().add(new TreeItem<>(pathElements.get(pathElements.size() - 1)));
		}
	}

	public TreeItem<String> getSelected() {
		return tree.getSelectionModel().getSelectedItem();
	}

	public Map<String, ClassNode> getNodes() {
		return nodes;
	}

	public boolean hasNode(String key) {
		return nodes.containsKey(key);
	}

	public Map<String, MappedClass> getRemap() {
		return remap;
	}

	public void goBack() {
		tree = new TreeView<String>(createTree(false));
		tree.setOnMouseClicked(getClickEvent());
		otherControls = create(tree);
		update();
	}

}
