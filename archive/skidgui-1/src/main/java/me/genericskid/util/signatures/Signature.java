package me.genericskid.util.signatures;

public class Signature
{
    private final int fieldTypes;
    private final int fieldCount;
    private final int fieldModifiers;
    private final int methodTypes;
    private final int methodCount;
    private final int methodModifiers;
    private final int methodParams;
    private final int parentCount;
    private final int classMods;
    private final int constructorCount;
    private final int constructorMods;
    private final int constructorParams;
    private final int parameterMods;
    private final String owner;
    
    public Signature(final String owner, final int cMods, final int constCount, final int constMods, final int constParams, final int pMods, final int fTypes, final int fCount, final int fMods, final int mTypes, final int mCount, final int mMods, final int mParams, final int parents) {
        this.owner = owner;
        this.classMods = cMods;
        this.parameterMods = pMods;
        this.constructorCount = constCount;
        this.constructorMods = constMods;
        this.constructorParams = constParams;
        this.fieldCount = fCount;
        this.fieldTypes = fTypes;
        this.fieldModifiers = fMods;
        this.methodCount = mCount;
        this.methodTypes = mTypes;
        this.methodModifiers = mMods;
        this.methodParams = mParams;
        this.parentCount = parents;
    }
    
    public int getConstructorCount() {
        return this.constructorCount;
    }
    
    public int getConstructorMods() {
        return this.constructorMods;
    }
    
    public int getConstructorParams() {
        return this.constructorParams;
    }
    
    public int getFieldTypes() {
        return this.fieldTypes;
    }
    
    public int getFieldCount() {
        return this.fieldCount;
    }
    
    public int getFieldModifiers() {
        return this.fieldModifiers;
    }
    
    public int getMethodTypes() {
        return this.methodTypes;
    }
    
    public int getMethodCount() {
        return this.methodCount;
    }
    
    public int getMethodModifiers() {
        return this.methodModifiers;
    }
    
    public int getMethodParams() {
        return this.methodParams;
    }
    
    public int getParameterMods() {
        return this.parameterMods;
    }
    
    public int getParentCount() {
        return this.parentCount;
    }
    
    public int getClassMods() {
        return this.classMods;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public String toSig() {
        return String.valueOf(this.parentCount) + ":" + this.constructorCount + ":" + this.constructorMods + ":" + this.constructorParams + ":" + this.parameterMods + ":" + this.methodCount + ":" + this.methodTypes + ":" + this.methodModifiers + ":" + this.methodParams + ":" + this.fieldCount + ":" + this.fieldModifiers + ":" + this.fieldTypes;
    }
}
