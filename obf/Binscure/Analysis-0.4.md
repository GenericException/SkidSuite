# Binscure 0.4

Binscure was a commercial Java obfuscator that had a running spat with Recaf. Its primary advertised features were its crasher abilities and intense flow/indy obfuscation. However as with most cat-and-mouse games, one side would give up. Since we're using past tense here, its Binscure if you haven't caught on. The ASM/RE tool crashers are now fully patched in Recaf with current versions. The flow and indy obfuscation still is fair for the current market.

## Common attributes

Binscure adds 3 classes to jars in the default package. They are typically single character names.

- String decryption
- Hosting ejected method bodies
- `ConcurrentHashMap` extension

## Control Flow

Most decompilers (CFR/FernFlower/Procyon) all choke on the control flow. Here's an example where the original was a default constructor (Meaning just one call to `super()`):
```java
    public BinarySearch() {
        int var10000 = .c.0 >= 0 ? (int)-529551166L ^ -176775740 : (int)1801211091L ^ -1716411626;

        while(true) {
            switch(var10000 ^ (int)2086348180L ^ -1503890599) {
            case -820085813:
                throw null;
            case -176900601:
                var10000 = (int)1801211091L ^ -1716411626;
                break;
            default:
                int var10001 = .c.1 != 0 ? (int)-586245205L ^ 660350973 : (int)-1889202819L ^ -727644576;

                while(true) {
                    switch(var10001 ^ (int)-1296865201L ^ 415327494) {
                    case -239729068:
                        super(); // <------------------------------- Here is the original super call, hard to see in the surrounding mess
                        return;
                    case 1344319775:
                        var10001 = (int)-1889202819L ^ -727644576;
                        break;
                    default:
                        throw null;
                    }
                }
            }
        }
    }
```

## Indy

Some implementations of features such as string-encryption, or the normal application logic if invoke-dynamic encryption is enabled, uses a "funnily" named bootstrap method:
```java
INVOKEDYNAMIC while ()V handle[H_INVOKESTATIC java/yeet. ̸̸̷͔̻̠̖̼̖̅̍ͭ̈̋ͩ̈́͊̏ͬͬ̆̀̋̍͐͛ͣ͘|̔̽̔ͥ̆̑̀ͨ̈̒̿҉̪͙̭̠̻̹̗͍͇̞̟͙̫̝̟̀ ̸̥͔̭͈̈̈̾ͮ̃̾̾͛̀̍͐ͦ̾ͨ̍̉̓̈̚͝͝s̷ͨ̂̇ͨ̓́̋͗̈̒̑ͩ̆͊͏̩̯̩̙̝̯̣̪͉̳̘e̡͋̋̄̄ͧ̃ͪͨ͠͡҉̪̱̥̤͇̹͔͎̫͔̯͜(I)V] args[]
INVOKEDYNAMIC fuck ()V handle[H_INVOKESTATIC a.a(IIIIIIIIIIIIIIIIIIIIIIII)Ljava/lang/Throwable;] args[]
INVOKEDYNAMIC yayeet ()Ljava/lang/YaYeet; handle[H_INVOKESTATIC a.a()[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[I] args[]
```

So yeah, `while` `fuck` `yeet` and `yayeet` are names that when paired together should tell you that Binscure is involved.

## String encryption

The string decryption class has 3 fields and 5 methods:
```java
// fields
    static int[] array;
    private int[][]..(Many levels of array go here)..[][] randomName; // Bogus just to screw with RE tools
    static StringDecryption singletonInstanceOfDecryptor;

// methods
    // String decrypt(String, int)
    // String decrypt(String) { decrypt(string, STATIC_INT_KEY); }
    // <init> // constructor
    // <clinit> // static initializer, populates 
    // <clinit> // duplicate dummy initializer which has an illegal argument in the descriptor
```

The methods will very often fail the decompile in most decompilers.

The static block that populates the `array` and `singletonInstanceOfDecryptor` fields looks roughly like:
```java
    static {
        new StringDecryption(); // assigns 'singletonInstanceOfDecryptor' value
        var2 = null;
        var4_1 = -1;
        var3_2 = null;
        v0 = 1192975198 ^ 1192975237; // 219
        var1_3 = new int[v0];
        StringDecryption.array = var1_3; // array assignment, values populated and used in decryption
        var0_4 = v0;
        try {
            var2 = new Random();
            return;
        }
        catch (RuntimeException v1) {
            block228: {
                v2 = var0_4;
                if (v2 < 0) {
                    return;
                }
                try {
                    do {
                        switch (v2) { 
                            /* giant table switch, the number of cases matches the value of 'v0' which here is 219  */
                        }
                        return;
                    } while (true);
                    return;
                }
                catch (IllegalMonitorStateException v3) {
                    var1_3[var0_4] = var4_1 ^ 1192975237;
                }
            }
        }
    }
```

## Crashers

For ASM and tool crashers, see [asm-crashing](../../obf-techniques/asm-crashing.md) for details.

Decompiler crash on inputs with heavy flow encryption, so its not technically a dedicated/targeted attack/feature.