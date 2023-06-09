# Scuti 0.0.1

Scuti is an open source Java obfuscator.

## Class Encryption / Loader

Scuti has an option to pack classes into binary blobs, and load them via a classloader. The binary blob names are just the original class names with XOR encoding, and the blob contents are the original class file with XOR encoding. The keys for both XOR encodings can be found in the `Loader` class:

```java
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Loader
extends ClassLoader {
    public static void main(String[] stringArray) throws Throwable {
        new Loader().loadClass("sample.string.StringsDummyApp")
             .getMethod("main", String[].class)
             .invoke(null, new Object[]{stringArray});
    }

    // decrypt contents of binary blobs
    private byte[] fileDecryptor(byte[] byArray) {
        byte[] byArray2 = new byte[byArray.length];
        int n = 0;
        while (n < byArray.length) {
            byArray2[n] = (byte)(byArray[n] ^ 0x45);
            ++n;
        }
        return byArray2;
    }
    
    // decrypt name of binary blobs
    private String stringDecryptor(String string) {
        String string2 = new String();
        int n = 0;
        while (n < string.length()) {
            string2 = String.valueOf(string2) + (char)(string.charAt(n) ^ 0x539);
            ++n;
        }
        return string2;
    }

    @Override
    public Class<?> loadClass(String string) throws ClassNotFoundException {
        try {
            // Lookup class in system first
            Class<?> clazz = null;
            try {
                clazz = ClassLoader.getSystemClassLoader().loadClass(string);
                if (clazz != null) {
                    return clazz;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            
            // Not found, check binary blobs
            if (clazz == null) {
                String string2 = new String(this.stringDecryptor(string.replace(".", "/")));
                URL uRL = this.getResource(string2);
                InputStream inputStream = uRL.openStream();
                byte[] byArray = this.fileDecryptor(Loader.toByteArray(inputStream));
                return this.defineClass(string, byArray, 0, byArray.length);
            }
            throw new ClassNotFoundException(string);
        }
        catch (Exception exception) {
            throw new ClassNotFoundException(string);
        }
    }

    // Common util function, you can ignore impl details here
    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        int n;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] byArray = new byte[65535];
        while ((n = inputStream.read(byArray)) != -1) {
            byteArrayOutputStream.write(byArray, 0, n);
        }
        byteArrayOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }
}
```

## Corrupt Names/Streams

I did not see any change in the sample jars after peeking at a few classes.

## Duplicate Variables

Not noticable in most decompilers.

## Flow

The default settings inserts opaque predicates that just do `throw null`.
```java
    private static int bbb;
    private static int aaa;

    static {
        bbb = 0x1746 ^ 0xFFFFBA4B; // -21235
        aaa = 0xFFFFE0A3 ^ 0xFFFFBA4B; // 23272
    }

    int binarySearch(int[] nArray, int n, int n2, int n3) {
        if (bbb > aaa) { // always false
            throw null;
        }
        if (n2 >= n) {
            int n4 = n + (n2 - n) / 2;
            if (nArray[n4] == n3) {
                return n4;
            }
            if (bbb >= aaa) { // always false
                throw null;
            }
            if (nArray[n4] > n3) {
                return this.binarySearch(nArray, n, n4 - 1, n3);
            }
            if (bbb >= aaa) { // always false
                throw null;
            }
            return this.binarySearch(nArray, n4 + 1, n2, n3);
        }
        if (bbb >= aaa) { // always false
            throw null;
        }
        return -1;
    }
```

## Flow Goto

Useless in most decompilers that offer block sorting.

## Flow Try

Useless in decompilers that remove dead code. CFR hides inserted dummy try blocks. Procyon does not.

```java
// sample from procyon output
if (BinarySearch.bbb > BinarySearch.aaa) {
    null;
    try {
        throw;
    }
    catch (ThrowableType throwableType) {
        throw throwableType;
    }
}
```

## Hide code

Just marks things as synthetic.

## Indy

Replaces references with invoke-dynamic backed lookups.
```java
    int binarySearch(int[] nArray, int n, int n2, int n3) {
        if (n2 >= n) {
            int n4 = n + (n2 - n) / 2;
            if (nArray[n4] == n3) {
                return n4;
            }
            if (nArray[n4] > n3) {
                return (int)BinarySearch.boot("name", "çõùäøñºùõàüºÖýúõæíÇñõæ÷ü", "öýúõæíÇñõæ÷ü", "¼ÏÝÝÝÝ½Ý", (Integer)1, (Object)this, (int[])nArray, (int)n, (int)(n4 - 1), (int)n3);
            }
            return (int)BinarySearch.boot("name", "çõùäøñºùõàüºÖýúõæíÇñõæ÷ü", "öýúõæíÇñõæ÷ü", "¼ÏÝÝÝÝ½Ý", (Integer)1, (Object)this, (int[])nArray, (int)(n4 + 1), (int)n2, (int)n3);
        }
        return -1;
    }
```

The lookup logic:
```java
    private static String decrypt(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        while (n < string.length()) {
            stringBuilder.append((char)(string.charAt(n) ^ 0x94));
            ++n;
        }
        return stringBuilder.toString();
    }

    public static CallSite boot(MethodHandles.Lookup lookup, String string, MethodType methodType, String clsName, String methodName, String methodDesc, Integer isStatic) {
        MethodType methodType2 = MethodType.fromMethodDescriptorString(BinarySearch.decrypt(methodDesc), BinarySearch.class.getClassLoader());
        try {
            if (isStatic == 1) {
                return new ConstantCallSite(lookup.findVirtual(Class.forName(BinarySearch.decrypt(clsName)), BinarySearch.decrypt(methodName), methodType2).asType(methodType));
            }
            return new ConstantCallSite(lookup.findStatic(Class.forName(BinarySearch.decrypt(clsName)), BinarySearch.decrypt(methodName), methodType2).asType(methodType));
        }
        catch (Exception exception) {
            return null;
        }
    }
```

## Num

Uses XOR operations to make constant numbers into expressions of multiple values.
```java
    int binarySearch(int[] nArray, int n, int n2, int n3) {
        if (n2 >= n) {
            int n4 = n + (n2 - n) / (0x1783 ^ 0xFFFFF724 ^ 0x29F8 ^ 0xFFFFC95D);
            if (nArray[n4] == n3) {
                return n4;
            }
            if (nArray[n4] > n3) {
                return this.binarySearch(nArray, n, n4 - (0x11D6 ^ 0xFFFFF0F0 ^ 0x4D36 ^ 0xFFFFAC11), n3);
            }
            return this.binarySearch(nArray, n4 + (0x1E20 ^ 0xFFFFFE15 ^ 0x61CD ^ 0xFFFF81F9), n2, n3);
        }
        return 0xFFFF9EED ^ 0xFFFFC6B4 ^ 0x27A5 ^ 0xFFFF8003;
    }
```

## String Encryption

Fast is just XOR
```java
    // calling convention: 3fea33a7afbbdcfa("obfuscated-text-here");
    private static String 3fea33a7afbbdcfa(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        int n = 0;
        while (n < string.length()) {
            stringBuilder.append((char)(string.charAt(n) ^ 0xF8));
            ++n;
        }
        return stringBuilder.toString();
    }
```

Strong is XOR as well, but used the calling method name in decryption
```java
    private static String 3f9059af63b93da0(String string, int n) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[1];
        String string2 = stackTraceElement.getMethodName() == null ? "" : stackTraceElement.getMethodName();
        int n2 = string2.hashCode();
        int n3 = n ^ n2;
        char[] cArray = new char[string.length()];
        int n4 = 0;
        while (n4 < string.length()) {
            cArray[n4] = (char)(string.charAt(n4) ^ n3);
            ++n4;
        }
        return new String(cArray);
    }
```

Calling convention on strong passes an extra XOR key value, and its obscured with a series of `INEG` instructions:
```java
3f9059af63b93da0("obf-string-here", -(-(-(-(-(-1348))))))
```