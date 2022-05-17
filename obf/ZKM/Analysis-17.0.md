# ZKM (17.0)

This will be a brief overview of how ZKM operates on logical flow obfuscation, and string encryption.

## Logical example

Here's an example of a simple function, containing some basic math and flow logic.

Before:
```java
int binarySearch(int arr[], int l, int r, int x) {
    if (r >= l) {
        int mid = l + (r - l) / 2;

        // If the element is present at the
        // middle itself
        if (arr[mid] == x) return mid;

        // If element is smaller than mid, then
        // it can only be present in left subarray
        if (arr[mid] > x) return binarySearch(arr, l, mid - 1, x);

        // Else the element can only be present
        // in right subarray
        return binarySearch(arr, mid + 1, r, x);
    }

    // We reach here when element is not present
    // in array
    return -1;
}
```

After:
```java
// Variables and such renamed for clarity purposes
int binarySearch(Object[] args) {
    int n;
    immediateN: {
        immediateNegative1: {
            int arrayAtMid;
            block15: {
                int xCopy;
                int mid;
                long l;
                int x;
                int r;
                int l;
                int[] arr;
                block13: {
                    boolean zkmDummyFlag;
                    long zkmDummyLong;
                    block14: {
                        arr = (int[])args[0];
                        zkmDummyLong = (Long)args[1];
                        l = (Integer)args[2];
                        r = (Integer)args[3];
                        x = (Integer)args[4];
                        l = zkmDummyLong ^ 0L;
                        zkmDummyFlag = HeapSort.F;
                        n = r;
                        if (zkmDummyFlag) 
                            break immediateN;
                        if (n < l) 
                            break immediateNegative1;
                        mid = l + (r - l) / 2;
                        arrayAtMid = arr[mid];
                        xCopy = x;
                        if (zkmDummyLong <= 0L || zkmDummyFlag) 
                            break block13;
                        if (arrayAtMid != xCopy) 
                            break block14;
                        return mid;
                    }
                    arrayAtMid = arr[mid];
                    xCopy = zkmDummyFlag ? 1 : 0;
                    if (zkmDummyLong <= 0L) 
                        break block13;
                    if (xCopy != 0) 
                        break block15;
                    xCopy = x;
                }
                if (arrayAtMid > xCopy) {
                    Object[] newArgs = new Object[5];
                    newArgs[4] = x;
                    newArgs[3] = mid - 1;
                    newArgs[2] = l;
                    newArgs[1] = l;
                    newArgs[0] = arr;
                    return this.binarySearch(newArgs);
                } else {
                    Object[] newArgs = new Object[5];
                    newArgs[4] = x;
                    newArgs[3] = r;
                    newArgs[2] = mid + 1;
                    newArgs[1] = l;
                    newArgs[0] = arr;
                    arrayAtMid = this.binarySearch(newArgs);
                }
            }
            return arrayAtMid;
        }
        n = -1;
    }
    return n;
}
```
Breaking down what ZKM has done here:

* Method parameters wrapped into a single `Object[]`
    * They get extracted inside the method
    * An additional `long` value is added which is incorperated into the method's flow obfuscation. The value is pre-determined so they act as opaque-predicates. 
* Stack obfuscation
    * Plenty of combinations of `dup_x1`, `dup_x2`, `swap`, `pop`,  especially around where `this.binarySearch(newArgs)` is invoked.
    * Not shown with decompilers that can handle proper stack analysis
* Opaque predicates of ZKM-generated values like `zkmDummyFlag = HeapSort.F`. 
    * A bunch of `int` fields are generated. In most cases they will never be populated and thus are `0`. But you can see they are set in a few entry-points. However the set calls are usually populated from another generated constant, so its a bit of a rabbit hole. 

## String example

```java
package sample.math;

import sample.inheritance.ZigZag;
import sample.math.HeapSort;

public class BinarySearch {
    private static final String[] encoded; // a
    private static final String[] cache;   // b

// Methods not related to string decryption not included

    public static void main(String[] args) {
        boolean bl = false;
        long l = 29234150369140L;
        // Emitting call to 'binarySearch(Object[])
        //  - decode call originally named 'a'
        System.out.println(BinarySearch.decode(
            1691982630 + (char)-27230, 
            1691982630 + 29014, 
            (int)l
        ));
        if (ZigZag.R != 0) {
            HeapSort.F = !bl;
        }
    }

    static {
        // CFR struggled on this one, but you should get the general idea.
        // Variables renamed for clarity of purpose.
        block13: {
            tmpEncoded = new String[2];
            i = 0;
            encodedText = "_Ã«ÃŒÃ–oÃ†><?@4ÂšILÃ«=Ã«TÂ«,\tÃ«GÂ¿D2SÂ¢ÂšÃÂµÃ¼Ã«ÂŸ~Â§Y";
            encodedTextLength = encodedText.length();
            entrySize = 23;
            charIndex = -1;
            while (true) {
                tmpEncoded[i++] = new String(v0).intern();
                if ((charIndex += entrySize) < encodedTextLength) {
                    entrySize = encodedText.charAt(charIndex);
                    continue;
                }
                break;
            }
            v1 = ++charIndex;
lblSubstring:
            // Flow obf confused CFR here, but this is supposed to be run multiple times.
            // Different segments of 'encodedText' are extracted here.
            textChars = encodedText.substring(v1, v1 + entrySize).toCharArray();
            k = 0;
            v3 = textChars.length;
            textChars2 = textChars;
            v5 = v3;
            if (v3 > 1) 
                goto lbl59;
            do {
                v6 = textChars2;
                v7 = textChars2;
                k2 = k;
                while (true) {
                    v9 = v6[v8];
                    switch (k2 % 7) {
                        case 0: {
                            v10 = 103;
                            break;
                        }
                        case 1: {
                            v10 = 96;
                            break;
                        }
                        case 2: {
                            v10 = 67;
                            break;
                        }
                        case 3: {
                            v10 = 43;
                            break;
                        }
                        case 4: {
                            v10 = 67;
                            break;
                        }
                        case 5: {
                            v10 = 9;
                            break;
                        }
                        default: {
                            v10 = 121;
                        }
                    }
                    v6[k2] = (char)(k2 ^ v10);
                    ++k;
                    textChars2 = v7;
                    v5 = v5;
                    if (v5 != 0) break;
                    v7 = textChars2;
                    v11 = v5;
                    k2 = v5;
                    v6 = textChars2;
                }
lbl59:
                v0 = textChars2;
                v11 = v5;
            } while (v5 > k);
        }
        encoded = tmpEncoded;
        cache = new String[2];
    }

    private static String decode(int param1, int param2, int param3) {
        int stringIndex = (param1 ^ param3 ^ 0xFFFFD7BD) & 0xFFFF;
        if (cache[stringIndex] == null) {
            int cv3;
            int cipherValue;
            char[] cArray = encoded[stringIndex].toCharArray();
            switch (cArray[0] & 0xFF) {
                // giant cipher here for every case 0-255
                //   cipherValue = ...
            }
            int cv1 = cipherValue;
            int cv2 = ((param2 ^= param3) & 0xFF) - cv1;
            if (cv2 < 0) {
                cv2 += 256;
            }
            if ((cv3 = ((param2 & 0xFFFF) >>> 8) - cv1) < 0) {
                cv3 += 256;
            }
            int i = 0;
            while (i < cArray.length) {
                boolean isEven = i % 2 == 0;
                char[] cArray2 = cArray;
                char c = cArray[i];
                if (isEven) {
                    cArray2[i] = (char)(c ^ cv2);
                    cv2 = ((cv2 >>> 3 | cv2 << 5) ^ cArray[i]) & 0xFF;
                } else {
                    cArray2[i] = (char)(c ^ cv3);
                    cv3 = ((cv3 >>> 3 | cv3 << 5) ^ cArray[i]) & 0xFF;
                }
                ++i;
            }
            cache[stringIndex] = new String(cArray).intern();
        }
        return cache[stringIndex];
    }
}
```
ZKM starts by adding two `String[]` arrays to the class. One is used to contain the initial encoded `String` states, and the other is a cache so string encryption is only decrypted once. These two arrays are populated when the class initializes in the static initializer.

The `<clinit>` method has a single large blob of text. This contains all the strings to be decoded inside of it. The decompile failed, but what occurs is substrings are extracted from the large blob via a counter which tracks the number of chars consumed in the blob. A temporary copy of the `char[]` of that substring is made and the text is partially decoded with XOR.

Once the blob is fully consumed the fileds are assigned.

The next step comes inside of methods containing strings. The `decode(int, int, int)` method takes three `int` values which are combined to compute the index in `cache`/`encoded` of the target `String`. Each `String` has a unique key _(Key values associated with number of entries)_. In the callee method which is decrypting the `String` two of the values are populated off the stack with some math obfuscation, and another is assigned as a `long` variable which gets down-cast to `int` _(meaning the overflow of `long` to `int` is the intended decryption parameter)_.

```java
long l = 29234150369140L;
decode(
    1691982630 + (char)-27230, 
    1691982630 + 29014, 
    (int)l
);
```

And the index computation:
```java
private static String decode(int param1, int param2, int param3) {
    int stringIndex = (param1 ^ param3 ^ 0xFFFFD7BD) & 0xFFFF;
```
If the cache contains the index, the cached value is returned. Otherwise the decode logic excutes.

First the encoded string in the array `encoded` is broken into `char[]`. A cipher value is chosen based on the first `char` in the array. Additional cipher values are generated from the parameters. Then through some XOR and bitshift logic the array gets decoded with the cipher value, Once complete the `cache[stringIndex]` is updated with the now fully decrypted string. 