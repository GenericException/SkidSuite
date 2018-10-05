### Decompilation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Helios** | Helios is a multi-tool that lets you decompile with multiple engines. _(see below)_ | [helios-standalone](https://github.com/helios-decompiler/standalone-app)
| **Bytecode-Viewer** | Bytecode-Viewer is a multi-tool similar to Helios, but the UI is based on the multi-document-interface layout. This allows viewing different decompilations side by side.<ul><li>_Supports Java 8_.</li></ul> | [bytecode-viewer](https://github.com/Konloch/bytecode-viewer) |
| **JAD** | JAD is an older compiler which is unable to decompile many modern Java features.<ul><li>_Supports Java 6_.</li></ul> | [mirror](http://www.javadecompilers.com/jad) |
| **JD** | JD is one of the most popular decompilers around. It is capable of producing very legible results on non-obfuscated jars. However simple obfuscation and modern java features _(Some generics will suffice)_ will cause it to crash. It can open modern classes as long as they do not use any modern features.<ul><li>_Supports Java 7_.</li></ul>| [jd](http://jd.benow.ca/) |
| **Procyon** | Procyon is an all around good decompiler. It is capable of more, but most only use this feature.  <ul><li> _Supports Java 9_.</li></ul>  | [procyon](https://bitbucket.org/mstrobel/procyon/src/default/) / [gui-luyten](https://github.com/deathmarine/Luyten) |
| **CFR** | CFR is a decompiler focusing on support of modern features _(lambdas, string switches, etc.)_. It has a couple dozen command-line arguments that can tackle different obfuscation strategies. These are documented on the CFR blog and also [here](https://col-e.github.io/Recaf/cfr.html).<ul><li>_Supports Java 9_.</li></ul> | [cfr](http://www.benf.org/other/cfr/) |
| **FernFlower** | Fernflower is an analytical decompiler maintained by the IntelliJ community.<ul><li>_Supports Java 9_.</li></ul> | [fernflower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) |
| **Krakatau** | Krakatau is a set of 3 bytecode tools, an assembler, disassembler, and decompiler. The decompiler does not focus on recreating the original code of a class, but rather takes some _"artistic liberties"_. This allows it to bypass minor obfuscation that would be shown with a pattern matching decompiler. <ul><li>_Requires Python 2.7_</li><li>_Supports Java 7, but (dis)assembler supports Java 10_.</li></ul> | [krakatau](https://github.com/Storyyeller/Krakatau) |
| **jadx** | Jadx is an android dex to Java decompiler capable of also doing simple deobfuscation. | [jadx](https://github.com/skylot/jadx) |
| **Javap** | Javap is the dissasembler provided in all releases of the JDK. It's simple to use and since its only showing you bytecode and not attempting to transform it into source code, you can trust the output. | [javap](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javap.html) | 

> [_(Back to README)_](README.md)
