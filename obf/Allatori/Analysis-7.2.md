# Allatori 7.2

Allatori is a commercial Java obfuscator with a very silly description on their homepage. They claim reversing their obfuscation is _"nigh on impossible"_ which is a massive stretch. The main key detail is that their obfuscator has a free trial, which everyone uses because its free.

## Control Flow

None of them have noticable changes to the demo application samples. In later versions on their changelog they note improvements to control flow. So maybe this behavior is just broken in this version.

### Low 

The lowest setting of control flow, which is the `control-flow` sample has almost no noticable changes.

### Normal

The middle setting of control flow, which is the `normal-extensive-control-flow` sample has almost no noticable changes.

### Maximum

The max setting of control flow, which is the `maximum-extensive-control-flow` sample has almost no noticable changes.

## String Encryption

The string encryption offers two versions, with toggles for being fast or strong.

When encryption is applied, a new class is added to the jar file that handles string decryption, and string consts are rewritten to point there. The new class contains the decryptor for both fast and strong versions, but only one is used based on the toggle.

In some instances, string encryption is _not_ applied at all. See `StringsDuplicates` for instance. This applies to all variants of String encryption.

```java
public class StringsDuplicates
{
    private static final String F = "Hello this is a duplicate string";
    
    public static void main(final String[] args) {
        final String s = "Hello this is a duplicate string";
        p("Hello this is a duplicate string");
        p(s);
        p(duplicate());
        p("Hello this is a duplicate string");
    }
    
    private static String duplicate() {
        return "Hello this is a duplicate string";
    }
    
    public static void p(final String s) {
        System.out.println(s);
    }
}
```

### V3 - Fast

Calling conventions: `InsertedClass.decryptFast("___|GF]\\U_I");` 
- Special case characters replaced with `_`
- Decryption class name in the sample is `l` 

Decryption is essentially XOR:
```java
// cfr decompilation, format may slightly differ in other decompilers
    public static String decryptFast(String s) {
        int n;
        char[] cArray = new char[s.length()];
        int n2 = n = cArray.length - 1;
        char[] cArray2 = cArray;
        int n3 = (2 ^ 5) << 4 ^ 4 << 1;
        while (n2 >= 0) {
            char c;
            char c2;
            int n4 = n--;
            n3 = (char)((char)(n4 ^ n3) & 0x3F);
            cArray2[n4] = c2;
            if (n < 0) break;
            int n5 = n--;
            n3 = (char)((char)(n5 ^ n3) & 0x3F);
            cArray2[n5] = c;
            n2 = n;
        }
        return new String(cArray2);
    }
```

### V3 - Strong

Calling conventions: `InsertedClass.decryptStrong("___|GF]\\U_I");` 
- Special case characters replaced with `_`
- Decryption class name in the sample is `h` 

Decryption is still essentially XOR, but with the added layer of requring information about the calling method using stack-traces:
```java
// cfr decompilation, format may slightly differ in other decompilers
    public static String decryptStrong(String arg0) {
        char[] cArray;
        block4: {
            int n;
            int n2;
            int n3;
            int n4;
            String string;
            int n5;
            block3: {
                StackTraceElement stackTraceElement = new RuntimeException().getStackTrace()[1];
                String string2 = stackTraceElement.getClassName() + stackTraceElement.getMethodName();
                n5 = string2.length() - 1;
                string = string2;
                n4 = n5;
                int n6 = arg0.length();
                cArray = new char[n6];
                n3 = (3 ^ 5) << 4 ^ (3 ^ 5) << 1;
                n2 = n6 - 1;
                if (!true) break block3;
                n = --n2;
                if (n < 0) break block4;
            }
            do {
                char c = string.charAt(n4);
                int n7 = n2;
                cArray[n7] = (char)(c ^ (arg0.charAt(n7) ^ n3));
                n3 = (char)(0x3F & (n3 ^ (n ^ c)));
                if (--n4 < 0) {
                    n4 = n5;
                }
                n = --n2;
            } while (n >= 0);
        }
        return new String(cArray);
    }
```

### V4 - Fast & Strong

The only noticable difference between V3 and V4 is that the string decryption methods are placed in a random existing class, rather than a new dedicated class.

```java
// 'V4 Strong' uses the same features, but the control flow is funnily enough actually more simple.
    public static String decryptStrong(String arg0) {
        int n;
        StackTraceElement stackTraceElement = new RuntimeException().getStackTrace()[1];
        String string = new StringBuffer(stackTraceElement.getClassName()).append(stackTraceElement.getMethodName()).toString();
        int n2 = arg0.length();
        int n3 = n2 - 1;
        char[] cArray = new char[n2];
        int n4 = 4 << 4 ^ (2 << 2 ^ 3);
        int cfr_ignored_0 = (3 ^ 5) << 4 ^ 5 << 1;
        int n5 = (2 ^ 5) << 3 ^ 2;
        int n6 = n = string.length() - 1;
        int n7 = n3;
        String string2 = string;
        while (n7 >= 0) {
            int n8 = n3--;
            cArray[n8] = (char)(n5 ^ (arg0.charAt(n8) ^ string2.charAt(n)));
            if (n3 < 0) break;
            int n9 = n3--;
            char c = cArray[n9] = (char)(n4 ^ (arg0.charAt(n9) ^ string2.charAt(n)));
            if (--n < 0) {
                n = n6;
            }
            n7 = n3;
        }
        return new String(cArray);
    }
```