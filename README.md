# SkidSuite 3

SkidSuite 3  _(SS3)_ is a collection of useful tools pertaining to reverse engineering of java applications. This collection may be updated to include SS3-specific tools in the future but will largely consist of externally managed projects.  

> The last iteration _(SS2)_ can be found here:
> * [FireMasterK](https://github.com/FireMasterK/SkidSuite2-Latest) 
> * [Alerithe](https://github.com/Alerithe/SkidSuite2)

If you have a useful tool, interesting article, or tool idea suggestion _(if it is simple/small)_, open an issue and I'll incorperate it.

***

## Suggested Tools

### Obfuscation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Java Deobfuscator**  | Java deobfuscator is a tool created by Samczun and [others](https://github.com/java-deobfuscator/deobfuscator/graphs/contributors). It provides an easy to use interface for removing obfuscation of popular tools such as: _[Allatori](http://www.allatori.com/), [ClassGuard](https://www.zenofx.com/classguard/), [DashO](https://www.preemptive.com/products/dasho/overview), [Stringer](https://jfxstore.com/stringer/), and [Zelix](https://www.zelix.com/) (Additional general obfuscation practices also supported)_ |  [deobfuscator](https://github.com/java-deobfuscator/deobfuscator) / [gui](https://github.com/java-deobfuscator/deobfuscator-gui)  |
| **Enigma** | Enigma is an easy to use manual remapper of jar applications. It allows you to remap classes and members similar to how you would refactor a class in an IDE. This however relies on the class being successfully decompiled by an old build of Procyon. | [enigma](https://www.cuchazinteractive.com/enigma/) |

### Decompilation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Helios** | Helios is a multi-tool that lets you decompile with multiple engines _(see below)_ | [helios-standalone](https://github.com/helios-decompiler/standalone-app)
| **Procyon** | <ul><li>Procyon is not just a library for decompilation, but seeing that its very good at it lots of people use it for that reason.</li><li> _Supports Java 9_.</li></ul>  | [procyon](https://bitbucket.org/mstrobel/procyon/src/default/) / [gui](https://github.com/deathmarine/Luyten) |
| **CFR** | <ul><li>CFR is a decompiler focusing on support of modern features _(lambdas, string switches, etc.)_. It has a couple dozen command-line arguments that can tackle different obfuscation strategies. These are documented on the CFR blog and also [here](https://col-e.github.io/Recaf/cfr.html).</li><li>_Supports Java 9_.</li></ul> | [cfr](http://www.benf.org/other/cfr/) |
| **FernFlower** | <ul><li>Fernflower is an analytical decompiler maintained by the IntelliJ community.</li><li>_Supports Java 9_.</li></ul> | [fernflower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine) |
| **Krakatau** | <ul><li>Krakatau is a set of 3 bytecode tools, an assembler, disassembler, and decompiler. The decompiler does not focus on recreating the original code of a class, but rather takes some _"artistic liberties"_. This allows it to bypass minor obfuscation that would be shown with a pattern matching decompiler. </li><li>_Requires Python 2.7_</li><li>_Supports Java 7, but (dis)assembler supports Java 10_.</li></ul> | [krakatau](https://github.com/Storyyeller/Krakatau) |

> **Note**: If you have the JDK installed you can always use [javap](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javap.html) to dissasemble class files.

### Editing

| Tool  | Description  | Link |
|-------|--------------|------|
| **Recaf** | Recaf is a java bytecode editor that supports up to Java 11. <ul><li>Plugin API for extra features</li><li>Good search API that supports regular expressions</li><li>Can edit live java applications _(attach API)_</li></ul> | [recaf](https://github.com/Col-E/Recaf) |
| **APK Studio** | Reverse engineering toolkit for Android _(Uses smali, which is a derivative of java bytecode)_ <ul><li>View embedded resources</li><li>Install APK's to connected devices</li></ul> | [apkstudio](https://github.com/vaibhavpandeyvpz/apkstudio)

***

***

## Successors to SS2

| Tool  | Replacement  | Link |
|-------|--------------|------|
| **SkidVisualizer** | Recaf has everything SkidVisualizer had and more. | [recaf](https://github.com/Col-E/Recaf) |
| **SkidScan** | Recaf has a plugin that searches for most of the same detections, though it cannot be exported to an HTML report. | [mw-scan](https://github.com/Col-E/Recaf-plugin-workspace/releases/tag/example-mal-scan) |
| **Skidfuscator** | Allatori's demo does a good job at obfuscation and doesn't require proof-of-intent via organization email requirements. | [allatori](http://www.allatori.com/) |
| **SkidHijack** | UDP-Java is a Java agent / injection framework. While it is initially targeted at Minecraft it can easily be configured to target other java applications like Runescape. | [udp-java](https://github.com/UnknownDetectionParty/UDP-Java/) | 
| **ObfuRemover** | Samczun's deobfuscator project is everything this was and better. | [deobfuscatpr](https://github.com/java-deobfuscator/deobfuscator) / [gui](https://github.com/java-deobfuscator/deobfuscator-gui) |
| **Mapping Utility** | n/a | n/a |
| **Jar Correlation** | n/a | n/a |

***

***

## Other links

| Link  | Description |
|-------|-------------|
| [Crackmes.one](https://crackmes.one/lasts) | You can practice your reverse engineering skills with these challenges. |
| [JVM 101](https://blog.takipi.com/jvm-architecture-101-get-to-know-your-virtual-machine/) | A great article for an introduction to how the jvm / java bytecode works. |
| [Objectweb ASM](https://asm.ow2.io/) | This is what I would consider the go-to library for manipulating Java bytecode. |
| [Allatori 3 decompiled](https://github.com/netindev/Allatori-v3.0) | An interesting peek into how Java obfuscation works using the BCEL library |
| [Jetbrains Blog - Trisha Gree](https://blog.jetbrains.com/idea/author/trishagee/) | Trisha Gee has an excellent series _"Java Annotated Monthly"_ documenting interesting events in Java each month. |
| [OpenJDK mirror](https://github.com/md-5/OpenJDK) | A mirror of the current OpenJDK repository. This contains the source code to the lastest java code-base. Javadocs are great but sometimes it helps to see how the code actually works. Perhaps you may discover something interesting while looking around. |
