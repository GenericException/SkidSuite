package me.genericskid.util.analyze;

public class AnalysisRules
{
    private final boolean inclusiveSearch;
    private final boolean exclusiveSearch;
    private final boolean caseSensitivem;
    private final String[] packages;
    private final String[] searches;
    
    public AnalysisRules(final String[] searches, final String[] packages, final boolean inc, final boolean exc, final boolean caseSens) {
        this.searches = searches;
        this.packages = packages;
        this.inclusiveSearch = inc;
        this.exclusiveSearch = exc;
        this.caseSensitivem = caseSens;
    }
    
    public boolean isInclusiveSearch() {
        return this.inclusiveSearch;
    }
    
    public boolean isExclusiveSearch() {
        return this.exclusiveSearch;
    }
    
    public boolean isCaseSensitivem() {
        return this.caseSensitivem;
    }
    
    public String[] getPackages() {
        return this.packages;
    }
    
    public String[] getSearches() {
        return this.searches;
    }
}
