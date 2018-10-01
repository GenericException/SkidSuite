### Decompilation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Helios** | Helios is a multi-tool that lets you decompile with multiple engines. _(see below)_ | [helios-standalone](https://github.com/helios-decompiler/standalone-app)
| **Bytecode-Viewer** | Bytecode-Viewer is a multi-tool similar to Helios, but with a very messy codebase and uses a GUI powered by Java Swing rather than JavaFX. |
| **JAD** | JAD is an extremely old compiler written in C++ unable to decompile many modern Java features. |
| **jadx** | jadx is a dex to Java decompiler capable of also doing simple deobfuscation. |
| **JD** | JD is a very fast decompiler capable of producing very legible results on non-obfuscated jars. It does not support many modern Java implementations often causing annoying crashes. |
| **Procyon** | Procyon is not just a library for decompilation, but seeing that its very good at it lots of people use it for that reason.<ul><li> _Supports Java 9_.</li></ul>  | [procyon](https://bitbucket.org/mstrobel/procyon/src/default/) / [gui-luyten](https://github.com/deathmarine/Luyten) |
| **CFR** | CFR is a decompiler focusing on support of modern features _(lambdas, string switches, etc.)_. It has a couple dozen command-line arguments that can tackle different obfuscation strategies. These are documented on the CFR blog and also [here](https://col-e.github.io/Recaf/cfr.html).<ul><li>_Supports Java 9_.</li></ul> | [cfr](http://www.benf.org/other/cfr/) |
| **FernFlower** | Fernflower is an analytical decompiler maintained by the IntelliJ community.<ul><li>_Supports Java 9_.</li></ul> | [fernflower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) |
| **Krakatau** | Krakatau is a set of 3 bytecode tools, an assembler, disassembler, and decompiler. The decompiler does not focus on recreating the original code of a class, but rather takes some _"artistic liberties"_. This allows it to bypass minor obfuscation that would be shown with a pattern matching decompiler. <ul><li>_Requires Python 2.7_</li><li>_Supports Java 7, but (dis)assembler supports Java 10_.</li></ul> | [krakatau](https://github.com/Storyyeller/Krakatau) |
| **Javap** | Javap is the dissasembler provided in all releases of the JDK. It's simple to use and since its only showing you bytecode and not attempting to transform it into source code, you can trust the output. | [javap](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javap.html) | 

> [_(Back to README)_](README.md)