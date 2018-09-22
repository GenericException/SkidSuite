### Deobfuscation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Java Deobfuscator**  | Java deobfuscator is a tool created by Samczun and [others](https://github.com/java-deobfuscator/deobfuscator/graphs/contributors). It provides an easy to use interface for removing obfuscation of popular tools such as: _[Allatori](http://www.allatori.com/), [ClassGuard](https://www.zenofx.com/classguard/), [DashO](https://www.preemptive.com/products/dasho/overview), [Stringer](https://jfxstore.com/stringer/), and [Zelix](https://www.zelix.com/) (Additional general obfuscation practices also supported)_ |  [deobfuscator](https://github.com/java-deobfuscator/deobfuscator) / [gui](https://github.com/java-deobfuscator/deobfuscator-gui)  |
| **Enigma** | Enigma is an easy to use manual remapper of jar applications. It allows you to remap classes and members similar to how you would refactor a class in an IDE. This however relies on the class being successfully decompiled by an old build of Procyon. Exporting classes in the default package prefixes the `none` package, which is really annoying. | [enigma](https://www.cuchazinteractive.com/enigma/) |
| **JRemapper** | JRemapper is another manual remapper with a lot of the same design approaches of Enigma, except with a more modern decompiler _(CFR 132)_. Despite this it should only be used if Enigma fails due to some UI issues. | [jremapper](https://github.com/Col-E/JRemapper) |
| **Stringer Verification Bypass** | Automatically removes integrity checks created by Stringer. This is targeted at Stringer 3.0.x, which is signifigantly outdated but people still use it since you can.. \*Ahem\* _"find it online"_, so its still useful. | [stringer-verification-bypass](https://github.com/GraxCode/stringer-verification-bypass) |
| **Zelix Killer** | Similar to JavaDeobfuscator in usage, but entierly focused on Zelix Klassmaster. Supported Zelix versions are 8 and 11. | [zelix-killer](https://github.com/GraxCode/zelixkiller) |
| **Java Unpacker** | While not technically deobfuscation, dumping is very similar and can be used as a supplement to obfuscation. So I'll include it. Supported packers are: <ul><li>CoreProtectEx</li><li>XMC2Ex</li><li>JCryptEx</li><li>JarProtectorEx</li></ul>| [java-unpacker](https://github.com/GraxCode/java-unpacker) |

> [_(Back to README)_](README.md)
