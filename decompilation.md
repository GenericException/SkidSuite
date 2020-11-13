## Decompilation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Helios** _(outdated)_ | Helios is a multi-tool that bundles CFR, Procyon, FernFlower, Krakatau, and Javap. | [helios-standalone](https://github.com/helios-decompiler/standalone-app)
| **Recaf** | Recaf is a multi-tool that bundles CFR, Procyon, and FernFlower in an easy to use UI. <ul><li>_Supports Java 16_.</li></ul> | [recaf](https://github.com/Col-E/Recaf)
| **Bytecode-Viewer** | Bytecode-Viewer is a multi-tool similar to Helios, but the UI is based on the multi-document-interface layout. This allows viewing different decompilations side by side.<ul><li>_Supports Java 11_.</li></ul> | [bytecode-viewer](https://github.com/Konloch/bytecode-viewer) |
| **The Java Disassembler** | A fork of Bytecode-Viewer that removes bloated features and boasts a powerful code-simplification process that automatically cleans up obfuscation. This in turn allows decompilers to more effectively do their job and print cleaner output. <ul><li>_Supports Java 11_.</li></ul> | [The Java Dissassembler](https://github.com/LLVM-but-worse/java-disassembler) |
| **JAD** | JAD is an older compiler which is unable to decompile many modern Java features.<ul><li>_Supports Java 6_.</li></ul> | [mirror](http://www.javadecompilers.com/jad) |
| **JD** | JD is one of the most popular decompilers around. It is capable of producing very legible results on non-obfuscated jars. However simple obfuscation and modern java features _(Some generics will suffice)_ will cause it to crash. It can open modern classes as long as they do not use any modern features.<ul><li>_Supports Java 11_.</li></ul>| [jd](http://jd.benow.ca/) |
| **Procyon** | Procyon is more than a decompiler, but is generally used for just that. Works well, but slightly slower than other decompilers.  <ul><li> _Supports Java 10_.</li></ul>  | [procyon](https://github.com/mstrobel/procyon) / [gui-luyten](https://github.com/deathmarine/Luyten) |
| **CFR** | CFR is a decompiler focusing on support of modern features _(lambdas, string switches, etc.)_. It has a couple dozen command-line arguments that can tackle different obfuscation strategies. These are documented on the [CFR blog](http://www.benf.org/other/cfr/). You can check out the source and report bugs on [Github](https://github.com/leibnitz27/cfr). <ul><li>_Supports Java 13_.</li></ul> | [cfr](http://www.benf.org/other/cfr/) |
| **FernFlower** | Fernflower is an analytical decompiler maintained by the IntelliJ community.<ul><li>_Supports Java 9_.</li></ul> | [fernflower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) |
| **Krakatau** | Krakatau is a set of 3 bytecode tools, an assembler, disassembler, and decompiler. The decompiler does not focus on recreating the original code of a class, but rather takes some _"artistic liberties"_. This allows it to bypass minor obfuscation that would be shown with a pattern matching decompiler. <ul><li>_Requires Python_</li><li>_Supports Java 7, but (dis)assembler supports Java 10_.</li></ul> | [krakatau](https://github.com/Storyyeller/Krakatau) |
| **jadx** | Jadx is an android dex to Java decompiler capable of also doing simple deobfuscation. | [jadx](https://github.com/skylot/jadx) |
| **Javap** | Javap is the dissasembler provided in all releases of the JDK. It's simple to use and since its only showing you bytecode and not attempting to transform it into source code, you can trust the output. | [javap](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javap.html) | 

### Relevant links

| Link  | Description |
|-------|-------------|
| [The Strengths and Behavioral Quirks of Java Bytecode Decompilers](https://arxiv.org/abs/1908.06895) | An academic paper that analyzes the most popular decompilers and compares them using objective metrics. |

> [_(Back to README)_](README.md)
