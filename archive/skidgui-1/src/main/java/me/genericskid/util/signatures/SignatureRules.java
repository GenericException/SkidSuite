package me.genericskid.util.signatures;

public class SignatureRules
{
    private final boolean netMCOnly;
    private final boolean ignoreZKMFields;
    private final boolean obfIDs;
    private final boolean clCheck;
    private final String[] packages;
    
    public SignatureRules(final boolean netMCOnly, final String[] packages, final boolean ignoreZKMFields, final boolean obfIDs, final boolean clCheck) {
        this.netMCOnly = netMCOnly;
        this.packages = packages;
        this.ignoreZKMFields = ignoreZKMFields;
        this.obfIDs = obfIDs;
        this.clCheck = clCheck;
    }
    
    public String[] getPackages() {
        return this.packages;
    }
    
    public boolean isInclusive() {
        return this.netMCOnly;
    }
    
    public boolean isIgnoringZKM() {
        return this.ignoreZKMFields;
    }
    
    public boolean containsObfIDs() {
        return this.obfIDs;
    }
    
    public boolean checkWithCLs() {
        return this.clCheck;
    }
}
