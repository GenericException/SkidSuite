### Deobfuscation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Java Deobfuscator**  | Java deobfuscator is a tool created by Samczun and [others](https://github.com/java-deobfuscator/deobfuscator/graphs/contributors). It provides an easy to use interface for removing obfuscation of popular tools such as: _[Allatori](http://www.allatori.com/), [ClassGuard](https://www.zenofx.com/classguard/), [DashO](https://www.preemptive.com/products/dasho/overview), [Stringer](https://jfxstore.com/stringer/), and [Zelix](https://www.zelix.com/) (Additional general obfuscation practices also supported)_ |  [deobfuscator](https://github.com/java-deobfuscator/deobfuscator) / [gui](https://github.com/java-deobfuscator/deobfuscator-gui)  |
| **Enigma** | Enigma is an easy to use manual remapper of jar applications. It allows you to remap classes and members similar to how you would refactor a class in an IDE. This however relies on the class being successfully decompiled by an old build of Procyon. Exporting classes in the default package prefixes the `none` package, which is really annoying. | [enigma](https://www.cuchazinteractive.com/enigma/) |
| **JRemapper** | JRemapper is another manual remapper with a lot of the same design approaches of Enigma, except with a more modern decompiler _(CFR 132)_. Despite this it should only be used if Enigma fails due to some UI issues. | [jremapper](https://github.com/Col-E/JRemapper) |

> [_(Back to README)_](README.md)