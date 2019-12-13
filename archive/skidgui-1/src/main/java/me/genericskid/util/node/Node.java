package me.genericskid.util.node;

public class Node
{
    private final String original;
    private final Node parent;
    private String newName;
    
    public Node(final String original, final Node parent) {
        this.original = original;
        this.newName = original;
        this.parent = parent;
    }
    
    public void revert() {
        this.newName = this.original;
    }
    
    public String getOldName() {
        return this.original;
    }
    
    public void setName(final String name) {
        this.newName = name;
    }
    
    public String getNewName() {
        return this.newName;
    }
    
    public Node getParent() {
        return this.parent;
    }
}
