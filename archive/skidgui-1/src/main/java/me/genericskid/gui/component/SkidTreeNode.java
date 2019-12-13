package me.genericskid.gui.component;

import me.genericskid.util.node.DefaultNode;
import me.genericskid.util.node.Node;
import javax.swing.tree.DefaultMutableTreeNode;

public class SkidTreeNode extends DefaultMutableTreeNode
{
    private static final long serialVersionUID = 1L;
    private final boolean hasNode;
    private final boolean hasFileData;
    private Object data;
    
    public SkidTreeNode(final Node node) {
        super(node.getNewName());
        this.data = node;
        this.hasNode = true;
        this.hasFileData = false;
    }
    
    public SkidTreeNode(final DefaultNode fd) {
        super(fd.getNewName());
        this.data = fd;
        this.hasNode = false;
        this.hasFileData = true;
    }
    
    public boolean hasNode() {
        return this.hasNode;
    }
    
    public boolean hasFileData() {
        return this.hasFileData || this.getChildCount() == 0;
    }
    
    public DefaultNode getNode() {
        return (DefaultNode)this.data;
    }
    
    public DefaultNode getFileData() {
        return (DefaultNode)this.data;
    }
}
