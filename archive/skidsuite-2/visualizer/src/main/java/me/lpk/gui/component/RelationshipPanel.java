package me.lpk.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.objectweb.asm.tree.ClassNode;

import me.lpk.mapping.MappedClass;
import me.lpk.mapping.MappingFactory;
import me.lpk.util.SearchUtil;

/**
 * Unsure of where to go with this...
 */
public class RelationshipPanel extends JPanel {
	private static final long serialVersionUID = 24151L;
	private Map<String, MappedClass> maps;
	private Map<Node, Rectangle> positions = new HashMap<Node, Rectangle>();
	private Node grabbed;

	public RelationshipPanel(Map<String, ClassNode> nodes, String start) {
		this.maps = MappingFactory.mappingsFromNodes(nodes);
		Node init = new Node(maps.get(start));
		positions.put(init, new Rectangle(0, 0, 0, 0));
		addMouseListener(new MouseClickListener());
		addMouseMotionListener(new MouseMotListener());
	}

	private void openContext(Node n, int x, int y) {
		JPopupMenu context = new JPopupMenu();
		if (n.parent == null && n.mc.hasParent()) {
			JMenuItem jmiAddParent = new JMenuItem("Add parent node");
			jmiAddParent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {

					Node parent = new Node(n.mc.getParent());
					positions.put(parent, new Rectangle(x + 100, y));
				}
			});
			context.add(jmiAddParent);
			JMenuItem jmiAddChildren = new JMenuItem("Add children nodes");
			jmiAddChildren.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int off = 0;
					for (MappedClass child : n.mc.getChildrenMap().values()) {
						Node childNode = new Node(child);
						n.children.add(childNode);
						positions.put(childNode, new Rectangle(x + 100 + off, y + off));
						off += 30;
					}
				}
			});
			context.add(jmiAddChildren);
			JMenuItem jmiAddInterfaces = new JMenuItem("Add interface nodes");
			jmiAddInterfaces.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int off = 0;
					for (MappedClass inter : n.mc.getInterfaces()) {
						Node parent = new Node(inter);
						n.interfaces.add(parent);
						positions.put(parent, new Rectangle(x + 100 + off, y + off));
						off += 30;
					}
				}
			});
			context.add(jmiAddInterfaces);
		}
		context.show(this, x, y);
	}

	class Node {
		private MappedClass mc;
		private Node parent;
		private List<Node> children = new ArrayList<Node>();
		private List<Node> interfaces = new ArrayList<Node>();
		public boolean grabbed;
		public int refs, offsetX, offsetY;

		public Node(MappedClass mc) {
			this.mc = mc;
			positions.put(this, new Rectangle(0, 0, 10, 10));
			refs = SearchUtil.findReferences(mc.getNode()).size();
		}

		public void move(int x, int y) {
			Rectangle r = positions.get(this);
			r.x = x - offsetX;
			r.y = y - offsetY;
		}

		public void draw(Graphics2D g) {
			Rectangle r = positions.get(this);
			int txtPadd = 3, line = 12, i = 1;
			int width = g.getFontMetrics(g.getFont()).stringWidth(mc.getNewName()) + txtPadd * 2;
			int height = 100;
			// Drawing
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(Color.black);
			g.drawRect(r.x - 1, r.y - 1, r.width + 1, r.height + 1);
			int titleY = r.y + line * i++;
			g.drawString(mc.getNewName(), r.x + txtPadd, titleY);
			i++;
			int fields = mc.getFields().size();
			int methods = mc.getMethods().size();
			int children = mc.getChildrenMap().size();
			int interfaces = mc.getInterfaces().size();
			if (fields > 0) {
				g.drawString("Fields: " + fields, r.x + txtPadd, r.y + line * i++);
			}
			if (methods > 0) {
				g.drawString("Methods: " + methods, r.x + txtPadd, r.y + line * i++);
			}
			if (children > 0) {
				g.drawString("Children: " + children, r.x + txtPadd, r.y + line * i++);
			}
			if (interfaces > 0) {
				g.drawString("Interfaces: " + interfaces, r.x + txtPadd, r.y + line * i++);
			}
			if (refs > 0) {
				String s = "Times Referenced: " + refs;
				width = Math.max(width, g.getFontMetrics(g.getFont()).stringWidth(s) + txtPadd * 2);
				g.drawString(s, r.x + txtPadd, r.y + line * i++);
			}
			r.setBounds(r.x, r.y, width, height);
			g.drawLine(r.x, titleY + line / 2, r.x + width, titleY + line / 2);
		}

		public void drawConnections(Graphics2D g) {
			Rectangle r = positions.get(this);
			if (parent != null) {
				Rectangle r2 = positions.get(parent);
				g.setColor(Color.GREEN);
				g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
			}
			for (Node n : interfaces) {
				Rectangle r2 = positions.get(n);
				g.setColor(Color.BLUE);
				g.drawLine(r.x + r.width / 2, r.y + r.height / 2, r2.x + r2.width / 2, r2.y + r2.height / 2);
			}
		}
	}

	class MouseMotListener implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			if (grabbed != null) {
				grabbed.move(x, y);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			if (grabbed != null) {
				grabbed.move(x, y);
			}
		}
	}

	class MouseClickListener implements MouseListener {

		@Override
		public void mousePressed(MouseEvent e) {
			int mode = e.getButton();
			int x = e.getX(), y = e.getY();
			if (mode == MouseEvent.BUTTON1) {
				grab(x, y);
			} else if (mode == MouseEvent.BUTTON3) {
				for (Node n : positions.keySet()) {
					Rectangle r = positions.get(n);
					boolean vert = y > r.y && y < r.y + r.height;
					boolean horz = x > r.x && x < r.x + r.width;
					if (vert && horz) {
						openContext(n, x, y);
						return;
					}
				}

			}
		}

		public void grab(int x, int y) {
			for (Node n : positions.keySet()) {
				Rectangle r = positions.get(n);
				boolean vert = y > r.y && y < r.y + r.height;
				boolean horz = x > r.x && x < r.x + r.width;
				if (vert && horz) {
					grabbed = n;
					grabbed.offsetX = x - r.x;
					grabbed.offsetY = y - r.y;
					grabbed.grabbed = true;
					return;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (grabbed != null) {
				grabbed.grabbed = false;
				grabbed = null;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}
	}

	@Override
	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D) gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.clearRect(0, 0, getWidth(), getHeight());
		for (Node n : positions.keySet()) {
			n.drawConnections(g);
		}
		for (Node n : positions.keySet()) {
			n.draw(g);
		}
		repaint();
	}
}
