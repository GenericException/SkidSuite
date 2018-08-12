# SkidSuite 3

SkidSuite 3  _(SS3)_ is a collection of useful tools pertaining to reverse engineering of java applications. This collection may be updated to include SS3-specific tools in the future but will largely consist of externally managed projects.  

> The last iteration _(SS2)_ can be found here:
> * [FireMasterK](https://github.com/FireMasterK/SkidSuite2-Latest) 
> * [Alerithe](https://github.com/Alerithe/SkidSuite2)

If you have a useful tool, interesting article, or tool idea suggestion _(if it is simple/small)_, open an issue and I'll incorperate it.

***

## Suggested Tools

### Deobfuscation

| Tool  | Description  | Link |
|-------|--------------|------|
| **Java Deobfuscator**  | Java deobfuscator is a tool created by Samczun and [others](https://github.com/java-deobfuscator/deobfuscator/graphs/contributors). It provides an easy to use interface for removing obfuscation of popular tools such as: _[Allatori](http://www.allatori.com/), [ClassGuard](https://www.zenofx.com/classguard/), [DashO](https://www.preemptive.com/products/dasho/overview), [Stringer](https://jfxstore.com/stringer/), and [Zelix](https://www.zelix.com/) (Additional general obfuscation practices also supported)_ |  [deobfuscator](https://github.com/java-deobfuscator/deobfuscator) / [gui](https://github.com/java-deobfuscator/deobfuscator-gui)  |
| **Enigma** | Enigma is an easy to use manual remapper of jar applications. It allows you to remap classes and members similar to how you would refactor a class in an IDE. This however relies on the class being successfully decompiled by an old build of Procyon. Exporting classes in the default package prefixes the `none` package, which is really annoying. | [enigma](https://www.cuchazinteractive.com/enigma/) |
| **JRemapper** | JRemapper is another manual remapper with a lot of the same design approaches of Enigma, except with a more modern decompiler _(CFR 132)_. The dark theme is a nice bonus as well. | [jremapper](https://github.com/Col-E/JRemapper) |


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

### Obfuscation

| Tool  | Description  | Price | Link |
|-------|--------------|-------|------|
| **Allatori** | Alltori is a decent obfuscator and has a demo readily available, so for that alone its a good goto. You can't beat free.  | <ul><li>Demo: Free</li><li>Full: $290+</li></ul> | [allatori](http://www.allatori.com/) |
| **DashO** | DashO is a step above Allatori in almost every department. The flow obfuscation is noticable in some obfuscators, the string encryption is more dynamic, and it has more features overall. Sadly it is not free. You can use the trial for one week before it expires. | <ul><li>Trial: 1 Week </li><li>Full: Varies, [~$2000](https://www2.cs.arizona.edu/~collberg/Teaching/620/2008/Assignments/tools/DashO/) </li></ul> | [dasho](https://www.preemptive.com/products/dasho/overview) |
| **Dexguard** | Dexguard is an Android application obfuscator, supports all the common features plus a few extra Android-specific abilities.  | <ul><li>Full: Varies</li></ul> | [dexguard](https://www.guardsquare.com/en/products/dexguard) |
| **JObf** | JObf is an open source obfuscator. | <ul><li>Free</li></ul> | [jobf](https://github.com/superblaubeere27/obfuscator) |
| **Proguard** | Proguard is one of the most common obfuscators out there. Its one of the oldest and most imporantly, its totally free. It really only supports name obfuscation and not much else. Ironically you can use it on obfuscated jars to weaken name obfuscation into something more workable. | <ul><li>Free</li></ul> | [proguard](https://www.guardsquare.com/en/products/proguard) |
| **Radon** | Radon is an open source obfuscator that is about on par with Allatori in terms of features.  | <ul><li>Free</li></ul> | [radon](https://github.com/ItzSomebody/Radon) |
| **Stringer** | Stringer is an unweildy beast in a few regards. The flow obfuscation on high settings is really strong and the string encryption can't be statically reveresed without a LOT of effort _(Essentially requires emulation)_. It boasts some other neat features others don't have like resource protection and reflection obfuscation. Sadly the cooler features are locked away under the enterprise license which I cannot find any information about online. Just assume it costs an arm and a leg.  | <ul><li>Demo: Requires company verification</li><li>Full: Varies</li></ul> | [stringer](https://jfxstore.com/stringer/) |
| **yGuard** | yGuard is similiar to proguard in that its more of a code-shrinker than of an obfuscator. But hey, its free. | <ul><li>Free</li></ul> | [yguard](https://www.yworks.com/products/yguard) |
| **Zelix** | Zelix is another oldie, and their website shows it. Despite the outdated website Zelix is another strong obfuscator with good string and flow encryption. For the features it gives the smallest license is an absolute steal. | <ul><li>Demo: Free, requires company email</li><li>Full: $240+</li></ul> | [zelix klassmaster](https://www.zelix.com/klassmaster/index.html) |

> **Note**: _"Varies"_ means that the price is not set in stone and is determined when you ask for a quote through their sales team. 


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

## Android

| Tool  | Replacement  | Link |
|-------|--------------|------|
| **APK Studio** | Reverse engineering toolkit for Android _(Uses smali, which is a derivative of java bytecode)_ <ul><li>View embedded resources</li><li>Install APK's to connected devices</li></ul> | [apkstudio](https://github.com/vaibhavpandeyvpz/apkstudio) |
| **Show Java** | Show Java is an android app that lets you decompiler other applications on your phone. You can choose from your installed applications or by browsing your file-system. Supported decompilers are: <ul><li>FernFlower</li><li>CFR 110</li><li>JaDX 0.6.1</li></ul> | [show-java](https://github.com/niranjan94/show-java) |
| **Simplify** | Simplify emulates code to optimize code. The functionality is generic so multiple obfuscators should be supported out-of-the-box. Functions: <ul><li>Constant value propagation</li><li>Dead code removal</li><li>Unreflection</li><li>Etc. _(Peephole)_</li></ul> | [simplify](https://github.com/CalebFenton/simplify) |
| **Platform Tools** | A small download containing useful tools such as Android-Bridge-Debug _(adb)_ and Systrace. With this you don't have to download the rest of the massive Android SDK. | [platform-tools](https://developer.android.com/studio/releases/platform-tools) |


***

***

## Other links

| Link  | Description |
|-------|-------------|
| [Crackmes.one](https://crackmes.one/lasts) | You can practice your reverse engineering skills with these challenges. |
| [JVM 101](https://blog.takipi.com/jvm-architecture-101-get-to-know-your-virtual-machine/) | A great article for an introduction to how the jvm / java bytecode works. |
| [JVM Internals](http://blog.jamesdbloom.com/JVMInternals.html) | Another great article documenting how the jvm works. This one gets down to the details a little more than _JVM 101_, so think of it as _JVM 201_. | 
| [Objectweb ASM](https://asm.ow2.io/) | This is what I would consider the go-to library for manipulating Java bytecode. |
| [Allatori 3 decompiled](https://github.com/netindev/Allatori-v3.0) | An interesting peek into how Java obfuscation works using the BCEL library |
| [Cracking Java bytecode encryption](https://www.javaworld.com/article/2077342/core-java/cracking-java-byte-code-encryption.html) | A short article on reverse engineering a simple *"encrypted classloader"* sample application. |
| [Jetbrains Blog - Trisha Gree](https://blog.jetbrains.com/idea/author/trishagee/) | Trisha Gee has an excellent series _"Java Annotated Monthly"_ documenting interesting events in Java each month. |
| [OpenJDK mirror](https://github.com/md-5/OpenJDK) | A mirror of the current OpenJDK repository. This contains the source code to the lastest java code-base. Javadocs are great but sometimes it helps to see how the code actually works. Perhaps you may discover something interesting while looking around. |
| [Solving warsaw’s Java Crackme 3](http://blog.rewolf.pl/blog/?p=856) | High quality writeup on reverse engineering a very complex crackme. |
