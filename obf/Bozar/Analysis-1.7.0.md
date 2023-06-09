# Bozar 1.7.0

Bozar is an open source Java obfuscator. You'll know its used because it _always_ adds a watermark class in the default package.

## Control Flow

Adds a bunch of opaquqe predicates through:

- Loops that only iterate once
- If statements that evaluate to `false` with junk inside
- If statements that evaluate to `true` with original application logic inside
- Switch statements, with only one `case` ever being called, the rest being junk

The main difference in heavy/light versions is how _much_ junk is generated.

```java
    protected static long Ꮹ = 0L; // opaque predicate value added to class by obfuscator

    int binarySearch(int[] arr, int l, int r, int x) {
        if (r < l) return -1;
        int mid = l + (r - l) / 2;
        if (arr[mid] == x) {
            return mid;
        }
        if (arr[mid] > x) {
            long l2 = Ꮸ;
            boolean bl = true;
            block6: while (true) {
                long l3;
                if (!bl || (bl = false) || !true) {
                    l2 = l3 / -6108945711391965010L;
                }
                switch ((int)l2) {
                    case -1566035797: {
                        return this.binarySearch(arr, l, mid - 1, x);
                    }
                    /* Never called
                    case -1327512084: {
                        l3 = 8052406313736647093L;
                        continue block6;
                    }
                    case -797033115: {
                        l3 = 6156016122987228181L;
                        continue block6;
                    }
                    case -22054236: {
                        l3 = 5811685873224198842L;
                        continue block6;
                    }*/
                }
                break;
            }
            return this.binarySearch(arr, l, mid - 1, x);
        }
        // Only looped once
        while (true) {
            long l4;
            long l5;
            // Never true
            if ((l5 = (l4 = Ꮸ - 2214769733184377489L) == 0L ? 0 : (l4 < 0L ? -1 : 1)) == false) {
                continue;
            }
            if (l5 == -1) {
                // This path is ALWAYS taken
                return this.binarySearch(arr, mid + 1, r, x);
            }
            l5 = 1129722251;
        }
    }
```

## Constant obfuscation (Numbers)

### Light

Replaces constants with opaque operations yielding the original values.
```java
    int binarySearch(int[] arr, int l, int r, int x) {
        if (r >= l) {
            int mid = l + (r - l) / ("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".length() >>> "\u0000\u0000".length());
            if (arr[mid] == x) {
                return mid;
            }
            if (arr[mid] > x) {
                return this.binarySearch(arr, l, mid - ("\u0000\u0000\u0000\u0000".length() >>> "\u0000\u0000".length()), x);
            }
            return this.binarySearch(arr, mid + ("\u0000\u0000\u0000\u0000".length() >>> "\u0000\u0000".length()), r, x);
        }
        return 0x53D5 ^ 0xFFFFAC2A;
    }
```

### Flow

Hides value assignments behind junk control flow operations.

> Seems odd to have a control flow obfuscation setting in the number obfuscator, outside of the independent flow control obfuscation feature itself.

Same method as the above:
```java
// decompiled with Procyon, CFR emits a lot of bogus/junk patterns that look nasty
    int binarySearch(final int[] arr, final int l, final int r, final int x) {
        if (r >= l) {
            final int n = r - l;
            final long n2 = lcmp(8068761760694071178L, 8721591606836087007L);
            int n3 = 0;
            while (true) {
                Label_0035: {
                    if (n2 != 0) {
                        n3 = (0x27F2 ^ 0x27F0);
                        break Label_0035;
                    }
                    n3 = 859192173;
                }
                if (n2 + 15947023 == -1362386392) {
                    continue;
                }
                break;
            }
            final int mid = l + n / n3;
            if (arr[mid] == x) {
                return mid;
            }
            if (arr[mid] > x) {
                final int n4 = mid;
                final long n5 = lcmp(8252992054414294496L, 3407774022140351570L);
                int n6 = 0;
                while (true) {
                    Label_0105: {
                        if (n5 != 0) {
                            n6 = (0x5D9F ^ 0x5D9E);
                            break Label_0105;
                        }
                        n6 = 226625551;
                    }
                    if (n5 + 1395659819 == -1704573883) {
                        continue;
                    }
                    break;
                }
                return this.binarySearch(arr, l, n4 - n6, x);
            }
            else {
                final int n7 = mid;
                final long n8 = lcmp(6462864038150067356L, 1269553647395787379L);
                int n9 = 0;
                while (true) {
                    Label_0160: {
                        if (n8 != 0) {
                            n9 = "\u0000\u0000\u0000\u0000".length() >>> "\u0000\u0000".length();
                            break Label_0160;
                        }
                        n9 = 1388902579;
                    }
                    if (n8 + 2102770664 == -51791555) {
                        continue;
                    }
                    break;
                }
                return this.binarySearch(arr, n7 + n9, r, x);
            }
        }
        else {
            final long n10 = lcmp(-4746914042547609772L, -6372241617884838072L);
            int n11 = 0;
            while (true) {
                Label_0208: {
                    if (n10 != 0) {
                        n11 = (0x3C40 ^ 0xFFFFC3BF);
                        break Label_0208;
                    }
                    n11 = 2037908912;
                }
                if (n10 + 744491367 == -1298295772) {
                    continue;
                }
                break;
            }
            return n11;
        }
    }
```

### Crasher

Adds a class with a very long obnoxious name like `com/0/0/0/0/0/0/0/.../0.class`. The path name itself can be thousands of characters long. The ZIP file does not contain directory entries for each path. If you were to use a tool that modifies the application and attempts to insert entries for all directories you get a much larger jar due to the 70ish _(for the local and central entries combined)_ bytes needed at minimum to represent a single ZIP entry.