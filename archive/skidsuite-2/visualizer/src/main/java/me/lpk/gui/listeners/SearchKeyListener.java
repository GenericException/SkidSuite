package me.lpk.gui.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.gui.EnumSearchType;
import me.lpk.gui.VisualizerWindow;
import me.lpk.gui.component.SearchResultEntry;
import me.lpk.util.SearchUtil;

public class SearchKeyListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent e) {
		handle(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	private void handle(KeyEvent e) {
		// When the user enteres text into the search bar and his enter
		if (VisualizerWindow.instance.getSearchText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
			List<SearchResultEntry> results = null;
			switch (EnumSearchType.byDisplayText(VisualizerWindow.instance.getSearchType())) {
			case CLASS_NAME:
				// Populate search results with classes with the search text
				results = SearchUtil.findClass(VisualizerWindow.instance.getSearchText());
				break;
			case CLASS_CHILDREN:
				// Populate search results with classes extending or implementing the class given by the search text
				results = SearchUtil.findChildrenOfClass(VisualizerWindow.instance.getSearchText());
				break;
			case CLASS_REF:
				// Populate search results with references to the class.
				ClassNode node2 = VisualizerWindow.instance.getNodes().get(VisualizerWindow.instance.getSearchText());
				if (node2 == null){
					return;
				}
				results = SearchUtil.findReferences(node2);
				break;
			case METHOD_NAME:
				// Populate search results with methods matching the given name.
				results = SearchUtil.findMethods(VisualizerWindow.instance.getSearchText(), false);
				break;
			case METHOD_DESC:
				// Populate search results with methods matching the given desc.
				results = SearchUtil.findMethods(VisualizerWindow.instance.getSearchText(), true);
				break;
			case FIELD_NAME:
				// Populate search results with fields matching the given name.
				results = SearchUtil.findFields(VisualizerWindow.instance.getSearchText(), false);
				break;
			case FIELD_DESC:
				// Populate search results with fields matching the given desc.
				results = SearchUtil.findFields(VisualizerWindow.instance.getSearchText(), true);
				break;
			case LDC:
				// Populate search strings containing the given text.
				results = SearchUtil.findStringsContaining(VisualizerWindow.instance.getSearchText());
				break;
			}
			if (results != null) {
				VisualizerWindow.instance.getResultPanel().clearResults();
				for (SearchResultEntry result : results) {
					VisualizerWindow.instance.getResultPanel().addResult(result);
				}
				VisualizerWindow.instance.getResultPanel().sort();
			}
		}
	}
}