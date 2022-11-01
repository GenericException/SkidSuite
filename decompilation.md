# Decompilation

## Multi-tools

| Tool  | Description  | Link |
|-------|--------------|------|
| **Recaf** | Recaf is a multi-tool that bundles CFR, Procyon, and FernFlower in an easy to use UI. It has contextual right-click actions and keybinds to make navigating code similar to an IDE.  | [recaf](https://github.com/Col-E/Recaf)|
| **Bytecode-Viewer** | Bytecode-Viewer is a multi-tool similar to Helios, but the UI is based on the multi-document-interface layout. This allows viewing different decompilations side by side. | [bytecode-viewer](https://github.com/Konloch/bytecode-viewer) |
| **The Java Disassembler** | A fork of Bytecode-Viewer that removes bloated features and boasts a powerful code-simplification process that automatically cleans up obfuscation. This in turn allows decompilers to more effectively do their job and print cleaner output. | [The Java Dissassembler](https://github.com/LLVM-but-worse/java-disassembler) |
| **Helios** | **DISCONTINUED** Helios is a multi-tool that bundles CFR, Procyon, FernFlower, Krakatau, and Javap. | [helios-standalone](https://github.com/helios-decompiler/standalone-app)|


## Individual

| Tool  | Description  | Link |
|-------|--------------|------|
| **CFR** | CFR is a decompiler focusing on support of modern features _(lambdas, string switches, etc.)_. It has a couple dozen command-line arguments that can tackle different obfuscation strategies. These are documented on the [CFR blog](http://www.benf.org/other/cfr/). You can check out the source and report bugs on [Github](https://github.com/leibnitz27/cfr). | [cfr](http://www.benf.org/other/cfr/) - [Source](https://github.com/leibnitz27/cfr) |
| **FernFlower** | FernFlower is an analytical decompiler maintained by the IntelliJ community. | [Source](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) |
| **JAD** | JAD is an older compiler which is unable to decompile many modern Java features. | [mirror](http://www.javadecompilers.com/jad) |
| **jadx** | Jadx is an android dex to Java decompiler capable of also doing simple deobfuscation. | [Source](https://github.com/skylot/jadx) |
| **Javap** | Javap is the dissasembler provided in all releases of the JDK. It's simple to use and since its only showing you bytecode and not attempting to transform it into source code, you can trust the output. | [javap](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javap.html) |
| **JD** | JD is one of the most popular decompilers around. It is capable of producing very legible results on non-obfuscated jars. However simple obfuscation and modern java features _(Some generics will suffice)_ will cause it to crash. It can open modern classes as long as they do not use any modern features. | [JD](http://java-decompiler.github.io/) - [Source](https://github.com/java-decompiler/jd-gui) |
| **Krakatau** | Krakatau is a set of 3 bytecode tools, an assembler, disassembler, and decompiler. The decompiler does not focus on recreating the original code of a class, but rather takes some _"artistic liberties"_. This allows it to bypass minor obfuscation that would be shown with a pattern matching decompiler. | [Source](https://github.com/Storyyeller/Krakatau) |
| **Procyon** | Procyon is more than a decompiler, but is generally used for just that. Works well, but slightly slower than other decompilers. | [gui-luyten](https://github.com/deathmarine/Luyten) - [Source](https://github.com/mstrobel/procyon)|
| **QuiltFlower** | QuiltFlower is a fork of FernFlower that fixes a variety of bugs and generally improves output quality. | [Source](https://github.com/QuiltMC/quiltflower) |

### Relevant links

| Link                                                         | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [Anatomy of a Java Decompiler](https://accu.org/journals/overload/22/119/benfield_1850/) | A journal written by the authors of CFR and Procyon on how Java decompilers work. |
| [The Strengths and Behavioral Quirks of Java Bytecode Decompilers](https://arxiv.org/abs/1908.06895) | An academic paper that analyzes the most popular decompilers and compares them using objective metrics. |
| [Step-by-Step Java Decompilation Example](https://jameshamilton.eu/research/step-step-java-decompilation-example) | An explanation, step-by-step, of how some example bytecode gets translated into legible Java code via decompilation. |

> [_(Back to README)_](README.md)
