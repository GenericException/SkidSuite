package me.lpk.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.BevelBorder;

import me.lpk.gui.VisualizerWindow;

public class SearchResultPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JList<String> list = new JList<String>();
	private final Map<String, SearchResultEntry> results = new HashMap<String, SearchResultEntry>();
	private int lastIndex = -1;

	public void setup() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setLayout(new BorderLayout());
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		list.setModel(listModel);
		list.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				// Double clicked
				// Once to set the index, and now its clicked again.
				if (list.getSelectedIndex() == lastIndex) {
					String path = getPath();
					if (path != null) {
						if (VisualizerWindow.instance.getDecompilePanel().getCurrentNode() == null
								|| !VisualizerWindow.instance.getDecompilePanel().getCurrentNode().name.equals(path)) {
							VisualizerWindow.instance.getDecompilePanel().decompile(path);
						}
						VisualizerWindow.instance.getDecompilePanel().setIndex(getSelectedSearchEntry());
						// TODO: Have text indexes in the ASMPanel saved where
						// fields/methods are located
						// SearchResultEntry can then find the correct index
						// based on their field/method included
						// Index opcodes too if it doesn't RIP performance
						//
						// Override Textifier to do all this automatically?
					}
				}
				lastIndex = list.getSelectedIndex();
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
		JScrollPane pane = new JScrollPane(list);
		pane.setPreferredSize(new Dimension(250, VisualizerWindow.instance.getHeight()));
		add(pane, BorderLayout.CENTER);
	}

	private String getPath() {
		SearchResultEntry sre = getSelectedSearchEntry();
		return sre == null ? null : sre.getTarget() == null ? null : sre.getTarget().name;
	}

	private SearchResultEntry getSelectedSearchEntry() {
		return results.get(list.getSelectedValue());
	}

	public void clearResults() {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		list.setModel(listModel);
	}

	public void addResult(SearchResultEntry result) {
		results.put(result.toString(), result);
		DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
		listModel.addElement(result.toString());
		list.setModel(listModel);
		list.invalidate();
	}

	/**
	 * Sorts the list results.
	 * 
	 * @author Torsten H. (<a href=
	 *         "http://www.codeproject.com/Questions/804653/How-to-sort-JList-of-element-while-new-item-is-add">
	 *         CodeProject</a>)
	 */
	public void sort() {
		DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
		List<String> list = Collections.list(listModel.elements());
		Collections.sort(list);
		listModel.clear();
		for (String o : list) {
			listModel.addElement(o);
		}
	}
}
