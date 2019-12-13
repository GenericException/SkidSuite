package me.genericskid.gui.component;

import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.JPanel;

public class BorderPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    
    public BorderPanel(final String name) {
        this.setName(name);
        final Color col = new Color(184, 207, 229);
        this.setBorder(BorderFactory.createLineBorder(col, 1));
    }
    
    public void paintComponent(final Graphics g) {
        final Color bg = this.getBackground();
        g.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 40));
        g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        g.setColor(new Color(0, 0, 0, 70));
        g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        final String name = (this.getName() != null) ? this.getName() : "ERR";
        g.setFont(new Font("Arial", 0, 12));
        g.setColor(new Color(50, 60, 100));
        g.drawString(name, 4, 15);
    }
}
