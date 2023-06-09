# JObf 1.9.3

JObf is an open source Java obfuscator, which due to the generic name is more commonly referred to by the author's name superblaubeere27 / sb27.

## Crasher

**Annotations**: Adds a bunch of very long annotations everywhere, which lags decompilers.

**Invalid signatures**: Creates illegally formatted generic signatures on classes/fields/methods. Decompilers that trust this data to follow the correct format can crash when trying to parse these invalid signatures.

## Flow obfuscation

### Bad Concat

No apparent changes in the demo jar were found.

### Bad Pop

Seems to just add basic `POP` usage to random places.
```java
LDC ""
INVOKEVIRTUAL java/lang/String.length()I
POP
```

### Mangle comparisons

No apparent changes in the demo jar were found

### Mangle local vars

Replaces `T` variables with references into an `T[]`, where `T` is a primitive. Objects are not subject to array compacting.

For `int`, here's an example from the `BinarySearch` class:
```java
    public static void main(String[] args) {
        int[] var6 = new int[3];
        BinarySearch ob = new BinarySearch();
        int[] arr = new int[]{2, 3, 4, 10, 40};

        // Original:
        //   int n = arr.length;
        //   int x = 10;
        //   int result = ob.binarySearch(arr, 0, n - 1, x);
        var6[0] = arr.length;
        var6[1] = 10;
        var6[2] = ob.binarySearch(arr, 0, var6[0] - 1, var6[1]);

        if (var6[2] == -1) {
            System.out.println("Element not present");
        } else {
            System.out.println("Element found at index " + var6[2]);
        }
    }
```

### Mangle return

Moves all `return` handling to a single point. Where any prior `return X` value was handled, it is replaced with `ISTORE <ret-val>` and `GOTO <ret-handler>`.

Interesting enough this made the `BinarySearch` into a 1-liner, ignoring the variable `mid` declaration and the `return`:
```java
// cfr decompilation, formatted for legibility
//
//    COND ?
//     A :
//     B
//
    int binarySearch(int[] arr, int l, int r, int x) {
        int mid;
        int n = r >= l ? 
            (arr[mid = l + (r - l) / 2] == x ? 
                 mid : 
                (arr[mid] > x ? 
                    this.binarySearch(arr, l, mid - 1, x) :
                    this.binarySearch(arr, mid + 1, r, x))) : 
            -1;
        return n;
    }
```
Normally CFR decompiles the method like this:
```java
    int binarySearch(int[] arr, int l, int r, int x) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (arr[mid] == x) {
                return mid; // Note that there are multiple return statements, but in the obfuscated version above, there is only one
            }
            if (arr[mid] > x) {
                return this.binarySearch(arr, l, mid - 1, x);
            }
            return this.binarySearch(arr, mid + 1, r, x);
        }
        return -1;
    }
```

### Mangle switch

This feature replaces `switch` tables with `if-else` chains.

An example from `StrinsDummyApp`:
```java
// Before obfuscation
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String s = null;
    p(WELCOME);
    while (s == null || !s.equals("5")) {
        p("1. Option 1");
        p("2. Option 2");
        p("3. Option 3");
        p("4. Option 4");
        p("5. Exit");
        s = sc.nextLine();
        switch (s) {
        case "1": 
            p("a");
            p("b");
            p("c");
            break;
        case "2": 
            p("aa");
            p("bb");
            p("cc");
            break;
        case "3": 
            p("aaa");
            p("bbb");
            p("ccc");
            break;
        case "4": 
            p("aaaa");
            p("bbbb");
            p("cccc");
            break;
        }
    }
    sc.close();
}
```
```java
// After obfuscation, CFR decompilation
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String s = null;
    StringsDummyApp.p(WELCOME);
    while (s == null || !s.equals("5")) {
        block14: {
            block13: {
                block12: {
                    block11: {
                        int n;
                        int n2;
                        block10: {
                            String string;
                            block9: {
                                block8: {
                                    block7: {
                                        block6: {
                                            StringsDummyApp.p("1. Option 1");
                                            StringsDummyApp.p("2. Option 2");
                                            StringsDummyApp.p("3. Option 3");
                                            StringsDummyApp.p("4. Option 4");
                                            StringsDummyApp.p("5. Exit");
                                            string = s = sc.nextLine();
                                            n2 = -1;
                                            n = string.hashCode();
                                            if (n == (0x47 ^ 0x76)) break block6;
                                            if (n == (0x30 ^ 0x29) << " ".length()) break block7;
                                            if (n == (0xB9 ^ 0x8A)) break block8;
                                            if (n == (0x25 ^ 0x28) << (" ".length() << " ".length())) break block9;
                                            break block10;
                                        }
                                        if (string.equals("1")) {
                                            n2 = 0;
                                        }
                                        break block10;
                                    }
                                    if (string.equals("2")) {
                                        n2 = 1;
                                    }
                                    break block10;
                                }
                                if (string.equals("3")) {
                                    n2 = 2;
                                }
                                break block10;
                            }
                            if (string.equals("4")) {
                                n2 = 3;
                            }
                        }
                        n = n2;
                        if (n == (" ".length() << ("   ".length() << " ".length()) & ~(" ".length() << ("   ".length() << " ".length())))) break block11;
                        if (n == " ".length()) break block12;
                        if (n == " ".length() << " ".length()) break block13;
                        if (n != "   ".length()) {
                            continue;
                        }
                        break block14;
                    }
                    StringsDummyApp.p("a");
                    StringsDummyApp.p("b");
                    StringsDummyApp.p("c");
                    continue;
                }
                StringsDummyApp.p("aa");
                StringsDummyApp.p("bb");
                StringsDummyApp.p("cc");
                continue;
            }
            StringsDummyApp.p("aaa");
            StringsDummyApp.p("bbb");
            StringsDummyApp.p("ccc");
            continue;
        }
        StringsDummyApp.p("aaaa");
        StringsDummyApp.p("bbbb");
        StringsDummyApp.p("cccc");
    }
    sc.close();
}
```
Alternative view from Procyon:
```java
// After obfuscation, Procyon decompilation
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String s = null;
    p(WELCOME);

    while(s == null || !s.equals("5")) {
        p("1. Option 1");
        p("2. Option 2");
        p("3. Option 3");
        p("4. Option 4");
        p("5. Exit");
        s = sc.nextLine();
        byte var4 = -1;
        int var5 = s.hashCode();
        if (var5 != (71 ^ 118)) {
            if (var5 != (48 ^ 41) << " ".length()) {
                if (var5 != (185 ^ 138)) {
                    if (var5 == (37 ^ 40) << (" ".length() << " ".length()) && s.equals("4")) {
                        var4 = 3;
                    }
                } else if (s.equals("3")) {
                    var4 = 2;
                }
            } else if (s.equals("2")) {
                var4 = 1;
            }
        } else if (s.equals("1")) {
            var4 = 0;
        }

        if (var4 != (" ".length() << ("   ".length() << " ".length()) & ~(" ".length() << ("   ".length() << " ".length())))) {
            if (var4 != " ".length()) {
                if (var4 != " ".length() << " ".length()) {
                    if (var4 == "   ".length()) {
                        p("aaaa");
                        p("bbbb");
                        p("cccc");
                    }
                } else {
                    p("aaa");
                    p("bbb");
                    p("ccc");
                }
            } else {
                p("aa");
                p("bb");
                p("cc");
            }
        } else {
            p("a");
            p("b");
            p("c");
        }
    }

    sc.close();
}
```

### Replace goto

Replaces `goto <foo>` with `if (null == null) --> <foo>` _(and similar opaque predicates)_

### Replace if

Extracts `if` comparisons and replaces those with calls to generated methods `(II)Z`.

```java
// replaces if (a == b)
// becomes if (foo(a,b))
    private static boolean foo(int n, int n2) {
        return n == n2;
    }
```

## HWID

Inserts HWID checks into the static initializer:
```java
    static {
        if (!Arrays.equals(BinarySearch.llI(), new byte[]{-17, -17, -17, -49, -17, -6, -2, -17, -2})) {
            JOptionPane.showMessageDialog(null, "Invalid HWID (sample/math/BinarySearch)");
            System.exit(-1);
            throw new Error();
        }
    }
```

## Indy

Invokedynamic obfuscation adds a lookup to the current class and replaces field and method references:
```java
    private static String[] llII; // vv
    private static Class[] llIl; // added by obfuscator
    
    static {
        BinarySearch.setup();
    }

    private static void setup() {
        llII = new String[6];
        BinarySearch.llII[5] = "java.lang.StringBuilder:toString:()Ljava/lang/String;:  ";
        BinarySearch.llII[2] = "java.io.PrintStream:println:(Ljava/lang/String;)V:  ";
        BinarySearch.llII[3] = "java.lang.StringBuilder:append:(Ljava/lang/String;)Ljava/lang/StringBuilder;:  ";
        BinarySearch.llII[1] = "java.lang.System:out:0:    ";
        BinarySearch.llII[4] = "java.lang.StringBuilder:append:(I)Ljava/lang/StringBuilder;:  ";
        BinarySearch.llII[0] = "sample.math.BinarySearch:binarySearch:([IIII)I:  ";
        llIl = new Class[1];
        BinarySearch.llIl[0] = PrintStream.class;
    }
    
    // Used by BootstrapMethodsAttribute
    // 
    // Example calling convention (bytecode format, indy ref name not relevant, see the matching handle)
    //  INVOKEDYNAMIC 0 (Lsample/math/BinarySearch;[IIII)I handle[H_INVOKESTATIC sample/math/BinarySearch.lIlI(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;] args[]
    private static CallSite lIlI(MethodHandles.Lookup lookup, String s, MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        try {
            String[] split = llII[Integer.parseInt(s)].split(":");
            Class<?> classIn = Class.forName(split[0]);
            String name = split[1];
            MethodHandle methodHandle = null;
            int length = split[3].length();
            if (length <= 2) {
                MethodType methodDesc = MethodType.fromMethodDescriptorString(split[2], BinarySearch.class.getClassLoader());
                methodHandle = length == 2 ? lookup.findVirtual(classIn, name, methodDesc) : lookup.findStatic(classIn, name, methodDesc);
            } else {
                Class typeLookup = llIl[Integer.parseInt(split[2])];
                methodHandle = length == 3 ? lookup.findGetter(classIn, name, typeLookup) : (length == 4 ? lookup.findStaticGetter(classIn, name, typeLookup) : (length == 5 ? lookup.findSetter(classIn, name, typeLookup) : lookup.findStaticSetter(classIn, name, typeLookup)));
            }
            return new ConstantCallSite(methodHandle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

// Obfuscated method references
    int binarySearch(int[] arr, int l, int r, int x) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (arr[mid] == x) {
                return mid;
            }
            if (arr[mid] > x) {
                return (int)BinarySearch.lIlI("0", (BinarySearch)this, (int[])arr, (int)l, (int)(mid - 1), (int)x);
            }
            return (int)BinarySearch.lIlI("0", (BinarySearch)this, (int[])arr, (int)(mid + 1), (int)r, (int)x);
        }
        return -1;
    }

// Obfuscated field and method references
    public static void main(String[] args) {
        BinarySearch ob = new BinarySearch();
        int[] arr = new int[]{2, 3, 4, 10, 40};
        int n = arr.length;
        int x = 10;
        CallSite result = BinarySearch.lIlI("0", (BinarySearch)ob, (int[])arr, (int)0, (int)(n - 1), (int)x);

        // Original:
        //   if (result == -1) System.out.println("Element not present");
        //   else System.out.println("Element found at index " + result);
        if (result == -1) {
            BinarySearch.lIlI("2", (PrintStream)((Object)BinarySearch.lIlI("1")), (String)"Element not present");
        } else {
            BinarySearch.lIlI("2", (PrintStream)((Object)BinarySearch.lIlI("1")), (String)((Object)BinarySearch.lIlI("5", (StringBuilder)((Object)BinarySearch.lIlI("4", (StringBuilder)((Object)BinarySearch.lIlI("3", (StringBuilder)new StringBuilder(), (String)"Element found at index ")), (int)result)))));
        }
    }
```

## Reference proxy

Kinda like indy obfuscation, but with generated methods instead of invokedynamic insns+lookup.

```java
    public static void main(String[] args) {
        BinarySearch ob = new BinarySearch();
        int[] arr = new int[]{2, 3, 4, 10, 40};
        int n = arr.length;
        int x = 10;
        int result = ob.binarySearch(arr, 0, n - 1, x);
        if (result == -1) {
            BinarySearch.lIIIIII(System.out, "Element not present");
        } else {
            BinarySearch.lIIIlII(System.out, BinarySearch.lIIIIll(BinarySearch.lIIIIlI(BinarySearch.lIIIIIl(new StringBuilder(), "Element found at index "), result)));
        }
    }

// Proxy for StringBuilder.append(String)
    public static StringBuilder lIIIIIl(Object lllllllllIIllll, String string) {
        Object object;
        try {
            object = null;
            Object object2 = new Class[]{String.class};
            object2 = StringBuilder.class.getMethod("append", (Class<?>)object2);
            ((Method)object2).setAccessible(true);
            Object[] objectArray = new Object[1];
            objectArray[0] = string;
            object = ((Method)object2).invoke(lllllllllIIllll, objectArray);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return (StringBuilder)object;
    }

// Proxy for StringBuilder.append(int)
    public static StringBuilder lIIIIlI(Object lllllllllIIlllI, int n) {
        Object object;
        try {
            object = null;
            Object object2 = new Class[]{Integer.TYPE};
            object2 = StringBuilder.class.getMethod("append", (Class<?>)object2);
            ((Method)object2).setAccessible(true);
            Object[] objectArray = new Object[]{n};
            object = ((Method)object2).invoke(lllllllllIIlllI, objectArray);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return (StringBuilder)object;
    }

// Proxy for StringBuilder.toString()
    public static String lIIIIll(Object object) {
        Object object2;
        try {
            object2 = null;
            Object object3 = new Class[]{};
            object3 = StringBuilder.class.getMethod("toString", (Class<?>)object3);
            ((Method)object3).setAccessible(true);
            Object[] objectArray = new Object[]{};
            object2 = ((Method)object3).invoke(object, objectArray);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return (String)object2;
    }

// Proxy for PrintStream.println(String)
    public static void lIIIlII(Object lllllllllIIllIl, String string) {
        try {
            Object var4_2 = null;
            Object object = new Class[]{String.class};
            object = PrintStream.class.getMethod("println", (Class<?>)object);
            ((Method)object).setAccessible(true);
            Object[] objectArray = new Object[1];
            objectArray[0] = string;
            ((Method)object).invoke(lllllllllIIllIl, objectArray);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

// Proxy for PrintStream.println(String)
//  - Yes, JObf made a duplicate for some reason
    public static void lIIIIII(Object lllllllllIlIIII, String string) {
        try {
            Object var4_2 = null;
            Object object = new Class[]{String.class};
            object = PrintStream.class.getMethod("println", (Class<?>)object);
            ((Method)object).setAccessible(true);
            Object[] objectArray = new Object[1];
            objectArray[0] = string;
            ((Method)object).invoke(lllllllllIlIIII, objectArray);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
```

## Numbers

### Default settings

```
// Before
int arr[] = { 2, 3, 4, 10, 40 };

// After
int[] arr = new int[0x25 ^ 0x20];
arr["".length()] = "  ".length();
arr[" ".length()] = "   ".length();
arr["  ".length()] = 0x79 ^ 0x7D;
arr["   ".length()] = 0x3F ^ 0x35;
arr[0xC1 ^ 0xC5] = 0x56 ^ 0x7E;
```

### And/Shift settings

Adds `<<` `>>` and `&` operations into the mix for the above.

### ToArray

Basically the same as before, but all numbers are pooled and initialized in a single setup method called from the static initializer. Numbers are then replaced with references to the generated array. This actually makes the code less of a hassle to reas than some other combinations of number obf settings.

```java
 private static final int[] lIll; // Added by obfuscator
 
    static {
        BinarySearch.setup();
    }

    private static void setup() {
        lIll = new int[9];
        BinarySearch.lIll[0] = "  ".length();
        BinarySearch.lIll[1] = " ".length();
        BinarySearch.lIll[2] = -" ".length();
        BinarySearch.lIll[3] = 0xBE ^ 0xBB;
        BinarySearch.lIll[4] = "".length();
        BinarySearch.lIll[5] = "   ".length();
        BinarySearch.lIll[6] = 0xA0 ^ 0xA4;
        BinarySearch.lIll[7] = 0x12 ^ 0x18;
        BinarySearch.lIll[8] = 0x99 ^ 0xB1;
    }
```

## String encryption

Seems to skip strings in `StringsLong` for some reason.

### Default

Without any extra setting enabled, string encryption populates a generated `String[]` in each class. Strings are then replaced with references to this array. The array is populated in a method called by the static initializer.

The obfuscator uses different decryption schemes randomly for strings. They all have a Base64 component, but use an additional scheme on top.

- XOR
- Blowfish
- DES

```java
public class StringsDummyApp {
    private static final String WELCOME;
    private static final String[] lI; // added by obfuscator, stored decrypted strings

    static {
        StringsDummyApp.lIlI();
        WELCOME = lI[23];
    }

    private static void lIlI() {
        lI = new String[24];
        StringsDummyApp.lI[0] = StringsDummyApp.xor("bU9oU2ptT2hTam1PaFNqbU9oU2ptT2hTam1PaFN3cFJ1TncHNxktGB03dU53cFJ1TmptT2hTanBSdU53cFJ1OhhwUnVOd3BSdU53bU9oU2ptUnVOd3AhGiMSBDocIBBwUnVOd3BPaFNqbU9oU2ptT2hTam1PaFNqbU9oU2ptT2hTag==", "PrUnW");
        StringsDummyApp.lI[1] = StringsDummyApp.des("Cuo5rY58m+o=", "SrmlE");
        StringsDummyApp.lI[2] = StringsDummyApp.des("vhuB+LbtOX2Sw5duYaqmcw==", "iHGSb");
        StringsDummyApp.lI[3] = StringsDummyApp.des("iGxVeNHBapUpmV3tC0X4sw==", "goTgo");
        StringsDummyApp.lI[4] = StringsDummyApp.blowfish("baR+r1hzLzwAebwKyZVKCg==", "UMBQV");
        StringsDummyApp.lI[5] = StringsDummyApp.des("J2kxJvEdUFrICQNmnKlxZA==", "cCyez");
        StringsDummyApp.lI[6] = StringsDummyApp.blowfish("F7ztaLUPoo4=", "mUabU");
        StringsDummyApp.lI[7] = StringsDummyApp.xor("Yw==", "Rfmrp");
        StringsDummyApp.lI[8] = StringsDummyApp.des("MSrAsHfPVmo=", "zTJGr");
        StringsDummyApp.lI[9] = StringsDummyApp.xor("WQ==", "jVreo");
        StringsDummyApp.lI[10] = StringsDummyApp.xor("XA==", "hNKuv");
        StringsDummyApp.lI[11] = StringsDummyApp.blowfish("76GATemXFaI=", "dkWcO");
        StringsDummyApp.lI[12] = StringsDummyApp.des("2ZxzGj2Hs9I=", "aIQYR");
        StringsDummyApp.lI[13] = StringsDummyApp.des("c09OvgRkp0A=", "thSUb");
        StringsDummyApp.lI[14] = StringsDummyApp.xor("GC8=", "yNHZD");
        StringsDummyApp.lI[15] = StringsDummyApp.des("YKNriui/Y0A=", "pXunB");
        StringsDummyApp.lI[16] = StringsDummyApp.des("62Jrit1f+rM=", "OhrnX");
        StringsDummyApp.lI[17] = StringsDummyApp.xor("JRgI", "DyiJe");
        StringsDummyApp.lI[18] = StringsDummyApp.blowfish("HFXIHVvzi7M=", "HQpaS");
        StringsDummyApp.lI[19] = StringsDummyApp.des("/BJkwDzxxf8=", "NQGTy");
        StringsDummyApp.lI[20] = StringsDummyApp.blowfish("56euQr3Xa9M=", "apuaM");
        StringsDummyApp.lI[21] = StringsDummyApp.blowfish("F0CGv373RIY=", "rELbP");
        StringsDummyApp.lI[22] = StringsDummyApp.des("Y9HhrYUxLMU=", "HBxfH");
        StringsDummyApp.lI[23] = StringsDummyApp.blowfish("WOhQTzV88DxY6FBPNXzwPFjoUE81fPA83EDwLSMgcJPhk4ujsZTOn2QCyBBRuGZXlJ5OoqZHojCQ5cz95tpg96z8mfzAMMWC9kuwS4TzkUY9H9e47g7ULGlBe62snzICfCSaanvO8JNY6FBPNXzwPFjoUE81fPA8WOhQTzV88Dz4rBIIu0Knxg==", "ZjflN");
    }

    private static String des(String obj, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8)), 8), "DES");
            Cipher des = Cipher.getInstance("DES");
            des.init(2, keySpec);
            return new String(des.doFinal(Base64.getDecoder().decode(obj.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String xor(String obj, String key) {
        obj = new String(Base64.getDecoder().decode(obj.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        char[] keyChars = key.toCharArray();
        int i = 0;
        for (char c : obj.toCharArray()) {
            sb.append((char)(c ^ keyChars[i % keyChars.length]));
            ++i;
        }
        return sb.toString();
    }

    private static String blowfish(String obj, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8)), "Blowfish");
            Cipher des = Cipher.getInstance("Blowfish");
            des.init(2, keySpec);
            return new String(des.doFinal(Base64.getDecoder().decode(obj.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
```

### AES

Adds an `AES` decryption schemeto the above example.

### Hidden

No noticable difference from the above sample.