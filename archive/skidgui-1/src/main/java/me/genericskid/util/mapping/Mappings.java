package me.genericskid.util.mapping;

public class Mappings
{
    private String originalName;
    private String newName;
    
    public Mappings(final String original, final String newName) {
        this.originalName = original;
        this.newName = newName;
    }
    
    public String getOriginalName() {
        return this.originalName;
    }
    
    public void setOriginalName(final String name) {
        this.originalName = name;
    }
    
    public String getNewName() {
        return this.newName;
    }
    
    public void setNewName(final String name) {
        this.newName = name;
    }
    
    private boolean noPackage() {
        return !this.originalName.contains("/");
    }
    
    public String toMapping() {
        return (this.noPackage() ? "CLASS none/" : "CLASS ") + this.originalName + " " + this.newName;
    }
}
