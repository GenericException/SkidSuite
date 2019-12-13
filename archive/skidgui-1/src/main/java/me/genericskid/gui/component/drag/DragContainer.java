package me.genericskid.gui.component.drag;

import java.util.Collections;
import java.util.Comparator;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.util.ArrayList;
import me.genericskid.util.ClassContainer;
import java.util.HashMap;
import javax.swing.JPanel;

public class DragContainer extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final HashMap<ClassContainer, DragBox> classMap;
    private ArrayList<DragBox> boxesTemp;
    private int x;
    private int y;
    
    public DragContainer() {
        this.classMap = new HashMap<>();
        this.boxesTemp = new ArrayList<>();
        this.x = 2;
        this.y = 0;
        this.setLayout(null);
    }
    
    public void addBox(final DragBox box) {
        this.boxesTemp.add(box);
        this.classMap.put(box.classContainer, box);
    }
    
    public void draw() {
        this.repaint();
    }
    
    public void paintComponent(final Graphics gg) {
        super.paintComponent(gg);
        final Graphics2D g = (Graphics2D)gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        for (final DragBox lastBox : this.classMap.values()) {
            if (lastBox == null) {
                continue;
            }
            if (lastBox.parent != null) {
                g.setColor(Color.black);
                g.drawLine(lastBox.getX() + lastBox.getWidth() / 2, lastBox.getY() + lastBox.getHeight() / 2, lastBox.parent.getX() + lastBox.parent.getWidth() / 2, lastBox.parent.getY() + lastBox.parent.getHeight() / 2);
                this.drawArrow(g, lastBox.getX() + lastBox.getWidth() / 2, lastBox.getY() + lastBox.getHeight() / 2, lastBox.parent.getX() + lastBox.parent.getWidth() / 2, lastBox.parent.getY() + lastBox.parent.getHeight() / 2);
            }
            for (final DragBox inter : lastBox.interfaces) {
                g.setColor(Color.blue);
                g.drawLine(lastBox.getX() + lastBox.getWidth() / 2, lastBox.getY() + lastBox.getHeight() / 2, inter.getX() + inter.getWidth() / 2, inter.getY() + inter.getHeight() / 2);
                this.drawArrow(g, lastBox.getX() + lastBox.getWidth() / 2, lastBox.getY() + lastBox.getHeight() / 2, inter.getX() + inter.getWidth() / 2, inter.getY() + inter.getHeight() / 2);
            }
        }
    }
    
    private void drawArrow(final Graphics g1, final int x1, final int y1, final int x2, final int y2) {
        final Graphics2D g2 = (Graphics2D)g1.create();
        final int size = 6;
        final double dx = x2 - x1;
        final double dy = y2 - y1;
        final double angle = Math.atan2(dy, dx);
        final int len = (int)Math.sqrt(dx * dx + dy * dy) / 2;
        final AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g2.transform(at);
        g2.fillPolygon(new int[] { len - size * 2, len - size, len - size, len - size * 2 }, new int[] { 0, size, -size, 0 }, 4);
    }
    
    public void showBoxes() {
        this.boxesTemp.sort(new Comparator<DragBox>() {
            @Override
            public int compare(final DragBox o1, final DragBox o2) {
                final int i1 = this.getParentCount(o1.classContainer.clazz);
                final int i2 = this.getParentCount(o2.classContainer.clazz);
                return Integer.compare(i2, i1);
            }

            public int getParentCount(Class<?> c) {
                int i;
                for(i = 0; c.getSuperclass() != null; c = c.getSuperclass(), ++i) {}
                return i;
            }
        });
        for (final DragBox box : this.boxesTemp) {
            box.setLocation(box.getX() + this.x, box.getY() + this.y + 2);
            this.add(box);
            this.y += box.getHeight();
            box.myIndex = this.getComponentCount() - 1;
        }
    }
}
