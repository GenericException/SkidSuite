package me.genericskid.gui.component.drag;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseMotionListener, MouseListener
{
    int lastX;
    int lastY;
    private final DragBox box;
    
    public MouseInput(final DragBox dragBox) {
        this.lastX = -1;
        this.lastY = -1;
        this.box = dragBox;
    }
    
    @Override
    public void mouseDragged(final MouseEvent e) {
        if (this.lastX < 0 || this.lastY < 0) {
            this.lastX = e.getX();
            this.lastY = e.getY();
        }
        final int scrollSpeedX = e.getX() - this.lastX;
        final int scrollSpeedY = e.getY() - this.lastY;
        this.box.update(scrollSpeedX, scrollSpeedY);
    }
    
    @Override
    public void mouseMoved(final MouseEvent e) {
        this.lastX = e.getX();
        this.lastY = e.getY();
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getButton() == 2) {
            this.box.open = !this.box.open;
        }
        else if (e.getButton() == 3) {
            final DragContextMenu m = new DragContextMenu(this.box);
            m.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
    }
}
