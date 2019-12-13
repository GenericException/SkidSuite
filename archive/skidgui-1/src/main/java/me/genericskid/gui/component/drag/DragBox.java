package me.genericskid.gui.component.drag;

import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import me.genericskid.util.ClassContainer;
import javax.swing.JPanel;

public class DragBox extends JPanel
{
    private static final long serialVersionUID = 1L;
    public int myIndex;
    public final ClassContainer classContainer;
    public List<DragBox> children;
    public List<DragBox> interfaces;
    public DragBox parent;
    private DragContainer container;
    public boolean open;
    public boolean simple;
    public int myX;
    public int myY;
    
    public DragBox(final ClassContainer clazz) {
        this.myIndex = -1;
        this.children = new ArrayList<>();
        this.interfaces = new ArrayList<>();
        this.myX = 0;
        this.myY = 0;
        this.classContainer = clazz;
        final MouseInput mi = new MouseInput(this);
        this.addMouseListener(mi);
        this.addMouseMotionListener(mi);
    }
    
    public void update(final int scrollX, final int scrollY) {
        this.myX += scrollX;
        this.myY += scrollY;
        final Component c = this.container.getComponent(this.myIndex);
        if (c != null) {
            c.setLocation(this.getX() + scrollX, this.getY() + scrollY);
        }
    }
    
    public void paintComponent(final Graphics gg) {
        final Graphics2D g = (Graphics2D)gg;
        final Font titleFont = new Font("Arial", 1, 18);
        final Font normal = new Font("Arial", 0, 14);
        g.setFont(titleFont);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int hei = titleFont.getSize() + 6;
        int wei = (int)this.getTextDim(this.classContainer.clazz.getName(), g).getWidth() + titleFont.getSize() / 2;
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.white);
        g.fillRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
        g.setColor(Color.black);
        g.drawString(this.simple ? this.classContainer.clazz.getSimpleName() : this.classContainer.clazz.getName(), 4, titleFont.getSize());
        g.setFont(normal);
        if (this.open) {
            g.drawLine(0, hei + normal.getSize() / 2 - 4, wei, hei + normal.getSize() / 2 - 4);
            int line = 1;
            hei += normal.getSize();
            Field[] declaredFields;
            for (int length = (declaredFields = this.classContainer.clazz.getDeclaredFields()).length, i = 0; i < length; ++i) {
                final Field f = declaredFields[i];
                f.setAccessible(true);
                final String s = "[" + (this.simple ? f.getType().getSimpleName() : f.getType().getName()) + "] " + f.getName();
                final int strWei = (int)this.getTextDim(s, g).getWidth() + titleFont.getSize() / 2;
                if (strWei > wei) {
                    wei = strWei;
                }
                g.drawString(s, 4, 2 + titleFont.getSize() + normal.getSize() * (line + 1));
                ++line;
                hei += normal.getSize();
            }
            ++line;
            g.drawLine(0, hei + normal.getSize() / 2 - 4, wei, hei + normal.getSize() / 2 - 4);
            hei += normal.getSize();
            Method[] declaredMethods;
            for (int length2 = (declaredMethods = this.classContainer.clazz.getDeclaredMethods()).length, j = 0; j < length2; ++j) {
                final Method m = declaredMethods[j];
                m.setAccessible(true);
                final String s = "[" + (this.simple ? m.getReturnType().getSimpleName() : m.getReturnType().getName()) + "] " + m.getName();
                final int strWei = (int)this.getTextDim(s, g).getWidth() + titleFont.getSize() / 2;
                if (strWei > wei) {
                    wei = strWei;
                }
                g.drawString(s, 4, titleFont.getSize() + normal.getSize() * (line + 1));
                ++line;
                hei += normal.getSize();
            }
        }
        this.setSize(wei, hei);
        if (this.myIndex == this.container.getComponentCount() - 1) {
            this.container.draw();
        }
    }
    
    public Rectangle getTextDim(final String s, final Graphics g) {
        final FontMetrics fm = g.getFontMetrics();
        return fm.getStringBounds(s, g).getBounds();
    }
    
    public void setParent(final DragContainer container) {
        this.container = container;
    }
}
