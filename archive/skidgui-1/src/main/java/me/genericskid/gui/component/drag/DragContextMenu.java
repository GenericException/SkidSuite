package me.genericskid.gui.component.drag;

import me.genericskid.gui.action.drag.ActionShowChildren;
import me.genericskid.gui.action.drag.ActionShowParents;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class DragContextMenu extends JPopupMenu
{
    private static final long serialVersionUID = 1L;

    public DragContextMenu(final DragBox box) {
        JMenuItem itemToggleDisplay = new JMenuItem("Toggle display");
        JMenuItem itemToggleSimple = new JMenuItem("Toggle simple");
        JMenuItem itemShowParents = new JMenuItem("Show parents");
        JMenuItem itemShowChildren = new JMenuItem("Show children");
        itemToggleDisplay.addActionListener(arg0 -> box.open = !box.open);
        itemToggleSimple.addActionListener(arg0 -> box.simple = !box.simple);
        itemShowParents.addActionListener(new ActionShowParents(box));
        itemShowChildren.addActionListener(new ActionShowChildren(box));
        this.add(itemToggleDisplay);
        this.add(itemToggleSimple);
        this.add(itemShowParents);
        this.add(itemShowChildren);
    }
}
