# Deobfuscation

These tools automatically remove obfuscation by different obfuscators and packers.

| Tool  | Description  | Links |
|-------|--------------|------|
| **Java Deobfuscator**  | Java deobfuscator is a tool created by Samczun and [others](https://github.com/java-deobfuscator/deobfuscator/graphs/contributors). It provides an easy to use interface for removing obfuscation of popular tools such as: _[Allatori](http://www.allatori.com/), [ClassGuard](https://www.zenofx.com/classguard/), [DashO](https://www.preemptive.com/products/dasho/overview), [Stringer](https://jfxstore.com/stringer/), and [Zelix](https://www.zelix.com/) (Additional general obfuscation practices also supported)_ |  [Deobfuscator](https://github.com/java-deobfuscator/deobfuscator) / [GUI](https://github.com/java-deobfuscator/deobfuscator-gui)  |
| **Stringer Verification Bypass** | Automatically removes integrity checks created by Stringer. This is targeted at Stringer 3.0.x, which is signifigantly outdated but people still use it since you can.. \*Ahem\* _"find it online"_, so its still useful. | [Stringer Verification Bypass](https://github.com/GraxCode/stringer-verification-bypass) |
| **Threadtear**  | Java deobfuscator is a tool created by Graxcode. It provides an easy to use graphical interface for removing obfuscation of popular tools such as: _[Allatori](http://www.allatori.com/), [Paramorphism](https://paramorphism.dev/), [DashO](https://www.preemptive.com/products/dasho/overview), [Stringer](https://jfxstore.com/stringer/), and [Zelix](https://www.zelix.com/) (Additional general obfuscation practices also supported)_ | [Threadtear](https://github.com/GraxCode/threadtear) |
| **Zelix Killer** | Similar to JavaDeobfuscator in usage, but entierly focused on Zelix Klassmaster. Supported Zelix versions are 8 and 11. | [Zelix Killer](https://github.com/GraxCode/zelixkiller) |
| **Java Unpacker** | While not technically deobfuscation, dumping is very similar and can be used as a supplement to obfuscation. So I'll include it. Supported packers are: <ul><li>CoreProtectEx</li><li>XMC2Ex</li><li>JCryptEx</li><li>JarProtectorEx</li></ul>| [Java Unpacker](https://github.com/GraxCode/java-unpacker) |
| **Maple IR** | Analyzes the control flow of bytecode and performs several optimization techniques, resulting in cleaner bytecode that still yields the same behavior. | [Maple IR](https://github.com/LLVM-but-worse/maple-ir) / [Whitepaper](https://github.com/LLVM-but-worse/maple-ir/blob/master/docs/maple-ir.pdf) |
| **Recaf** | Recaf is a bytecode editor, but also patches most ASM crashing exploits automatically. | [Recaf](https://github.com/Col-E/Recaf) |

### Remapping

These tools remap classes in application jars in a similar way that the refactoring feature works in most IDE's. They're more focused in their purpose so remapping is actually a much easier on a large scale using these tools rather than decompiling code and using an IDE's refactor function.

| Tool  | Description  | Link |
|-------|--------------|------|
| **JRemapper** | Uses CFR decompiler. It can export a mappings JSON or a mapped Jar file. | [JRemapper](https://github.com/Col-E/JRemapper) |
| **Enigma** | Uses Procyon decompiler. It can export a mappings text in the Enigma/SRG/Tiny formats or a mapped Jar file. | [Enigma](https://github.com/FabricMC/Enigma) |

> [_(Back to README)_](README.md)
