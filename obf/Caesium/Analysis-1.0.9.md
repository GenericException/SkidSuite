# Caesium 1.0.9

Caesium is an open source Java obfuscator.

## String encryption

String encryption adds two `String[]` fields per class with strings, and populates those arrays in a method called by the static initializer. Fields and methods added use `int` values as their names.

```java
public class StringsLong {
    // The field private static String HELLO in the original application has been removed
    private static String[] 1332381194 = new String[6]; // decoded strings, updated by decrypt method
    private static String[] -875665860 = new String[6]; // encoded strings, populated in setup method
    private static long -231445683;

    static {
        StringsLong.setup(); // renamed from -1640252266
    }

    private static void setup() {
        int n;
        -231445683 = -747048824075094577L;
        long l = -231445683 ^ 0x6E867DC94622FCC1L;
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
        byte[] byArray = new byte[8];
        byArray[0] = (byte)(l >>> 56);
        for (n = 1; n < 8; ++n) {
            byArray[n] = (byte)(l << n * 8 >>> 56);
        }
        cipher.init(2, (Key)secretKeyFactory.generateSecret(new DESKeySpec(byArray)), new IvParameterSpec(new byte[8]));
        n = 1;
        block7: for (int i = 0; i < n; ++i) {
            switch (i) {
                case 0: {
                    // String constants here are shortened by 95% for legibility
                    StringsLong.-875665860[0] = "4s==";
                    StringsLong.-875665860[1] = "g1Yct3ezmhKR+1sqmScbAuc5oFq12wa==";
                    StringsLong.-875665860[2] = "pRsjTZn43k=";
                    StringsLong.-875665860[3] = "NlCK/R/KTsU7b/uG02rDaQNQ==";
                    StringsLong.-875665860[4] = "Sxa=";
                    StringsLong.-875665860[5] = "IdzQea4stjHxiajNQ==";
                    continue block7;
                }
                case 1: {
                    // String constants here are shortened by 95% for legibility
                    StringsLong.-875665860[0] = "4ysi";
                    StringsLong.-875665860[1] = "g1skQ==";
                    StringsLong.-875665860[2] = "psgsvomA==";
                    StringsLong.-875665860[3] = "Nlsss==";
                    StringsLong.-875665860[4] = "SxZds=";
                    StringsLong.-875665860[5] = "IdsQ==";
                    continue block7;
                }
                case 2: {
                    StringsLong.-875665860[0] = "kFm2bseZ4wFvazQyDFiHAMnmMDrSH5Rs";
                    continue block7;
                }
                case 4: {
                    StringsLong.-875665860[0] = "RsJvusLpERxVjrmEesnHBw==";
                }
            }
        }
    }

    public static void main(String[] a) {
        StringsLong.long1();
        StringsLong.long2();
        StringsLong.long3();
        StringsLong.long4();
        StringsLong.long5();
        StringsLong.longest();
    }

// Showcases how strings are referenced, after obfuscation (not too bad depending on decompiler, the visually ugly stuff is in the setup)

    private static void longest() {
        // Bytecode pattern of call seen below:
        //   ICONST_0
        //   LDC -747048824075094577L
        //   LDC 18L
        //   LXOR
        //   INVOKEDYNAMIC 1130100550 (IJ)Ljava/lang/String; handle[H_INVOKESTATIC sample/string/StringsLong.886994295(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/Object;] args[]
        //   INVOKESTATIC sample/string/StringsLong.p(Ljava/lang/String;)V
        // Breakdown of the call arguments:
        //                                  decrypt(key1,        index,    key2)
        StringsLong.p((String)StringsLong.886994295("1130100550", (int)0, (long)(0xF5A1F30D732175CFL ^ 0x12L)));
    }

    private static void long5() {
        //                                  decrypt(key1,        index,    key2)
        StringsLong.p((String)StringsLong.886994295("-1507168711", (int)1, (long)-747048824075094563L));
    }

    private static void long4() {
        //                                  decrypt(key1,        index,    key2)
        StringsLong.p((String)StringsLong.886994295("124391890", (int)(2 & 0xFFFFFFFF), (long)-747048824075094563L));
    }

    private static void long3() {
        //                                  decrypt(key1,        index,    key2)
        StringsLong.p((String)StringsLong.886994295("-1602833197", (int)3, (long)(0xF5A1F30D732175CFL ^ 0x12L)));
    }

    private static void long2() {
        //                                  decrypt(key1,        index,    key2)
        StringsLong.p((String)StringsLong.886994295("-1509198117", (int)4, (long)(0xF5A1F30D732175CFL ^ 0x12L)));
    }

    private static void long1() {
        //                                  decrypt(key1,        index,    key2)
        StringsLong.p((String)StringsLong.886994295("-270870517", (int)(5 & 0xFFFFFFFF), (long)-747048824075094563L));
    }

    public static void p(String s) {
        System.out.println(s);
    }

    // String decrypt function
    private static Object 886994295(MethodHandles.Lookup lookup, String string, MethodType methodType) {
        try {
            return new MutableCallSite(lookup.findStatic(StringsLong.class, "-1895167488", MethodType.fromMethodDescriptorString("(IJ)Ljava/lang/String;", StringsLong.class.getClassLoader())).asType(methodType));
        }
        catch (Exception exception) {
            throw new RuntimeException("sample/string/StringsLong:" + string + ":" + methodType.toString(), exception);
        }
    }

    private static String -1895167488(int n, long l) {
        l ^= 0x12L;
        l ^= 0x6E867DC94622FCC1L;
        if (1332381194[n] == null) {
            SecretKeyFactory secretKeyFactory;
            Cipher cipher;
            try {
                cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                secretKeyFactory = SecretKeyFactory.getInstance("DES");
            }
            catch (Exception exception) {
                throw new RuntimeException("sample/string/StringsLong");
            }
            byte[] byArray = new byte[8];
            byArray[0] = (byte)(l >>> 56);
            for (int i = 1; i < 8; ++i) {
                byArray[i] = (byte)(l << i * 8 >>> 56);
            }
            cipher.init(2, (Key)secretKeyFactory.generateSecret(new DESKeySpec(byArray)), new IvParameterSpec(new byte[8]));
            StringsLong.1332381194[n] = new String(cipher.doFinal(Base64.getDecoder().decode(-875665860[n])));
        }
        return 1332381194[n];
    }
}

```

## Reference mutation (Indy)

Replaces method references with invokedynamic lookups. Class and method names are encoded with Base64. Method descriptors are not.

No noticable difference is seen between light/normal versions.

```java
    public static void main(String[] args) {
        BinarySearch ob = new BinarySearch();
        int[] arr = new int[]{2, 3, 4, 10, 40};
        int n = arr.length;
        int x = 10;
        Object result = BinarySearch.1039741646("1872950773", (Object)-602810635, (Object)"c2FtcGxlLm1hdGguQmluYXJ5U2VhcmNo", "YmluYXJ5U2VhcmNo", "([IIII)I", 1064577680, 27, "-1135000298", 0.5731805f, "LTIxMDU1OTAyODI=", ob, arr, (int)0, (int)(n - 1), (int)x);
        if (result == -1) {
            //                                                                 java.io.PrintStream             println        
            BinarySearch.1039741646("1450198769", (Object)-602810635, (Object)"amF2YS5pby5QcmludFN0cmVhbQ==", "cHJpbnRsbg==", "(Ljava/lang/String;)V", 1056116464, 1037636784, 83374780, "730554395", 0.51080287f, System.out, "Element not present");
        } else {
            BinarySearch.1039741646("-1583588483", (Object)-602810635, (Object)"amF2YS5pby5QcmludFN0cmVhbQ==", "cHJpbnRsbg==", "(Ljava/lang/String;)V", 0.8178571566570189, "8+\f4", 3.028041556876062, 8.468316494354863, 37, System.out, BinarySearch.1039741646("-726887297", (Object)-602810635, (Object)"amF2YS5sYW5nLlN0cmluZ0J1aWxkZXI=", "dG9TdHJpbmc=", "()Ljava/lang/String;", -0.7889894300592272, 111, "1133727079", "289567825", 0.7195613f, BinarySearch.1039741646("-198955584", (Object)-602810635, (Object)"amF2YS5sYW5nLlN0cmluZ0J1aWxkZXI=", "YXBwZW5k", "(I)Ljava/lang/StringBuilder;", 0.09646261f, -1766978133879695022L, 3.769532305053609, " .0\u000081+/\n;", 84, BinarySearch.1039741646("841140373", (Object)-602810635, (Object)"amF2YS5sYW5nLlN0cmluZ0J1aWxkZXI=", "YXBwZW5k", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", 48, "2092671911", "MTU4MjQwMjU4Mg==", "08\u0000\n", 3330358363791547931L, new StringBuilder(), "Element found at index "), (int)result)));
        }
    }

    public static Object 1039741646(MethodHandles.Lookup lookup, String string, MethodType methodType, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object clazz, Object object7, Object object8) throws Exception {
        int n = (Integer)object;
        n = (n ^ -1691513236) & 0xFF;
        object5 = 1226797323;
        clazz = Class.forName(new String(DatatypeConverter.parseBase64Binary((String)object2)));
        object7 = MethodType.fromMethodDescriptorString((String)object4, clazz.getClassLoader());
        if (n == (Integer)object5) {
            return new MutableCallSite(lookup.findStatic(clazz, new String(DatatypeConverter.parseBase64Binary((String)object3)), (MethodType)object7).asType(methodType));
        }
        return new MutableCallSite(lookup.findVirtual(clazz, new String(DatatypeConverter.parseBase64Binary((String)object3)), (MethodType)object7).asType(methodType));
    }
```

## Number obfuscation

Consolidates number references and assigns them to static fields. These fields are themselves given names that follow `int` values which leads to confusing visuals in decompilers.

```java
public class BinarySearch {
    private static int 1451334047 = Integer.reverse(0x40000000);
    private static int -386380037 = Integer.reverse(Integer.MIN_VALUE);
    private static int -1904958518 = Integer.reverse(Integer.MIN_VALUE);
    private static int 592680131 = Integer.reverse(-1);
    private static int 661541894 = 40 >>> 3 | 40 << -3;
    private static int -212873713 = 0 >>> 187 | 0 << ~187 + 1;
    private static int -1091681971 = 1024 >>> 41 | 1024 << -41;
    private static int -461616979 = Integer.reverse(Integer.MIN_VALUE);
    private static int -1504530941 = Integer.reverse(-1073741824);
    private static int 810445733 = 1024 >>> 73 | 1024 << ~73 + 1;
    private static int -403044060 = 8 >>> 97 | 8 << -97;
    private static int -1450395855 = Integer.reverse(-1073741824);
    private static int -1450676097 = -1610612736 >>> 156 | -1610612736 << -156;
    private static int 436255700 = 1024 >>> 232 | 1024 << ~232 + 1;
    private static int 1777463581 = (0x50000000 >>> 89 | 0x50000000 << -89) & 0xFFFFFFFF;
    private static int 394532862 = 80 >>> 99 | 80 << ~99 + 1;
    private static int -1225731661 = 0 >>> 173 | 0 << -173;
    private static int -1206233622 = Integer.reverse(Integer.MIN_VALUE);
    private static int -984781178 = (-1 >>> 84 | -1 << -84) & 0xFFFFFFFF;

    public static void main(String[] args) {
        BinarySearch ob = new BinarySearch();
        int[] nArray = new int[661541894]; // Actually BinarySearch.661541894, which is '40 >>> 3 | 40 << -3' simplified to '5'
        nArray[BinarySearch.-212873713] = -1091681971;  // Value assignment is actually BinarySearch.-1091681971
        nArray[BinarySearch.-461616979] = -1504530941;  // Value assignment is actually BinarySearch.-1504530941
        nArray[BinarySearch.810445733] = -403044060;    // Value assignment is actually BinarySearch.403044060
        nArray[BinarySearch.-1450395855] = -1450676097; // Value assignment is actually BinarySearch.-1450676097
        nArray[BinarySearch.436255700] = 1777463581;    // Value assignment is actually BinarySearch.1777463581
        int[] arr = nArray;
        int n = arr.length;
        int x = 394532862; Actually BinarySearch.394532862
        int result = ob.binarySearch(arr, -1225731661, n - -1206233622, x); // also field refs, not real numbers
        if (result == -984781178) { // And again
            System.out.println("Element not present");
        } else {
            System.out.println("Element found at index " + result);
        }
    }
}
```