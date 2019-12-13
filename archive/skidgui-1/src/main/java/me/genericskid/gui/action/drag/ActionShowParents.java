package me.genericskid.gui.action.drag;

import me.genericskid.util.ClassContainer;
import me.genericskid.gui.component.drag.DragBox;
import me.genericskid.gui.frames.panel.impl.RelationshipPanel;

public class ActionShowParents extends DragActionListener
{
    public ActionShowParents(final RelationshipPanel panel) {
        super(panel);
    }
    
    public ActionShowParents(final DragBox box) {
        super(box);
    }
    
    @Override
    protected void genrateContainers(final Class<?> originClazz) {
        this.addContainer(new ClassContainer(originClazz));
        for (Class<?> c2 = originClazz.getSuperclass(); c2 != null && !c2.equals(Object.class); c2 = null) {
            this.addContainer(new ClassContainer(c2));
            c2 = c2.getSuperclass();
            if (c2 != null && c2.equals(Object.class)) {}
        }
    }
}
