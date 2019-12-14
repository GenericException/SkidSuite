## Relevant articles

### Reverse Engineering - Resources

| Link  | Description |
|-------|-------------|
| [Crackmes.one](https://crackmes.one/lasts) | You can practice your reverse engineering skills with these challenges. |
| [Allatori 3 decompiled](https://github.com/netindev/Allatori-v3.0) | An interesting peek into how Java obfuscation works using the BCEL library |
| [Objectweb ASM](https://asm.ow2.io/) | The go-to library for manipulating Java bytecode. |
| [ByteBuddy](http://bytebuddy.net/) | A runtime code-generation library built off of ASM. Additionally it tries to remain user friendly by abstracting away java bytecode constructs. |
| [VisualVM](https://github.com/oracle/visualvm) | VisualVM is an All-in-One Java troubleshooting tool. You can use features like thread dumping to assist in reverse engineering unknown files by checking thread stack-traces. |

### Reverse Engineering - Information

| Link  | Description |
|-------|-------------|
| [JVM 101](https://blog.takipi.com/jvm-architecture-101-get-to-know-your-virtual-machine/) | A great article for an introduction to how the jvm / java bytecode works. |
| [JVM Internals](http://blog.jamesdbloom.com/JVMInternals.html) | Another great article documenting how the jvm works. This one gets down to the details a little more than _JVM 101_, so think of it as _JVM 201_. | 
| [Cracking Java bytecode encryption](https://www.javaworld.com/article/2077342/core-java/cracking-java-byte-code-encryption.html) | A short article on reverse engineering a simple *"encrypted classloader"* sample application. |
| [Solving warsaw’s Java Crackme 3](http://blog.rewolf.pl/blog/?p=856) | High-quality writeup on reverse engineering a very complex crackme. |
| [CFR](http://benf.org/other/cfr/) | Lee Benfield writes about the Java class file layout and its relationship with his decompiler. Some of the articles make for quick interesting reads. |
| [What is Obfuscation and how does it apply to Java?](https://www.preemptive.com/obfuscation) | An article by PreEmptive _(DashO and Dotfuscator team)_ that covers some of the common tactics used by obfuscators with illustrative example graphics. |
| [Decompiler Vulnerabilities and Bugs](https://github.com/Janmm14/decompiler-vulnerabilities-and-bugs) | A collection of tricks that fool decompilers into emitting the wrong output, or crashing entierly. |
| [Stop Decompiling My Java](https://github.com/ItzSomebody/StopDecompilingMyJava) | A continuation of _"Decompiler Vulnerabilities and Bugs"_. |
| [Anatomy of a Java Decompiler](https://accu.org/index.php/journals/1850) | An article by the authors of CFR & Procyon discussing some of the basic concepts that go into creating a Java decompiler. |

### General Java

| Link  | Description |
|-------|-------------|
| [OpenJDK mirror](https://github.com/md-5/OpenJDK) | A mirror of the current OpenJDK repository. This contains the source code to the lastest java code-base. Javadocs are great but sometimes it helps to see how the code actually works. Perhaps you may discover something interesting while looking around. |
| OpenJDK <ul><li>[9](http://openjdk.java.net/projects/jdk9/)</li><li>[10](http://openjdk.java.net/projects/jdk/10/)</li><li>[11](http://openjdk.java.net/projects/jdk/11/)</li><li>[12](http://openjdk.java.net/projects/jdk/12/)</li><li>[13](http://openjdk.java.net/projects/jdk/13/)</li><li>[14](http://openjdk.java.net/projects/jdk/14/)</li></ul>| OpenJDK project pages for each JDK release. Each page contains links to the JEPs _(JDK Enhancement Proposals)_ in the version. Following these pages is useful for staying up-to-date with the status of the Java language. You can also find pending proposals on the site that are not tied to any JDK release yet which can sometimes have some interesting goals. |
| [Java Specifications](https://docs.oracle.com/javase/specs/) | Oracle's languange and JVM specifications for each version of Java released. |
| [/r/Java](https://www.reddit.com/r/java/) | The Java subreddit. Topics focus on relevant libraries/frameworks and version update news. |
| [FX Experience](http://fxexperience.com/) | FX Experience hosts blogposts for JavaFX news and also hosts a few projects of its own. |
| [Migrating from Java 8 to Java 11](https://blog.joda.org/2018/09/from-java-8-to-java-11.html) | Stephen Colebourne's blogpost on upgrading Java 8 code to support the new modular system and account for deprecated libraries. |
| [Jetbrains Blog - Trisha Gree](https://blog.jetbrains.com/idea/author/trishagee/) | Trisha Gee has an excellent series _"Java Annotated Monthly"_ documenting interesting events in Java each month. |

> [_(Back to README)_](README.md)
