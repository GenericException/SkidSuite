package me.genericskid.util.node;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultNode extends Node
{
    private HashMap<String, DefaultNode> children;
    private ArrayList<DefaultNode> files;
    
    public DefaultNode(final String original, final DefaultNode parent) {
        super(original, parent);
        this.children = new HashMap<>();
        this.files = new ArrayList<>();
    }
    
    public DefaultNode(final String original, final DefaultNode parent, final HashMap<String, DefaultNode> children, final ArrayList<DefaultNode> files) {
        this(original, parent);
        this.children = children;
        this.files = files;
    }
    
    public void revertNames(final boolean revertFilenames) {
        for (final DefaultNode n : this.children.values()) {
            n.revertNames(revertFilenames);
        }
        this.revert();
        if (revertFilenames) {
            for (final DefaultNode fd : this.files) {
                fd.revert();
            }
        }
    }
    
    public ArrayList<DefaultNode> getFiles() {
        return this.files;
    }
    
    public void addFile(final DefaultNode fileData) {
        this.files.add(fileData);
    }
    
    public Collection<DefaultNode> getChildren() {
        return this.children.values();
    }
    
    public boolean hasChild(final String folder) {
        return this.children.containsKey(folder);
    }
    
    public DefaultNode getChild(final String folder) {
        return this.children.get(folder);
    }
    
    public void addChild(final String folder) {
        this.children.put(folder, new DefaultNode(folder, this));
    }
    
    public void addChild(final DefaultNode node) {
        this.children.put(node.getOldName(), node);
    }
    
    public void addChildOverride(final DefaultNode node) {
        this.children.remove(node.getOldName());
        this.children.put(node.getOldName(), node);
    }
    
    public void setChildren(final Collection<DefaultNode> children) {
        this.children.clear();
        this.addChildren(children);
    }
    
    public void addChildren(final Collection<DefaultNode> children) {
        for (final DefaultNode n : children) {
            this.addChild(n);
        }
    }
}
