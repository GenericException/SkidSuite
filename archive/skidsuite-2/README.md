# SkidSuite 2

SkidSuite 2 is a collection of useful tools pertaining to reverse engineering of Java applications.  

***

## Table of Contents

|  Project   | Description |
|------------|-------------|
| Core       | Base utility logic. |
| Analysis   | Method stack simulation/analysis. |
| AntiOb     | Deobfuscation of obfuscated programs. Includes: <ul><li>Intelligent auto-naming</li><li>Removal of string encryption of popular obfuscators [ZKM/Allatori/DashO]</li></ul>|
| Hijack     | A java agent allowing the rewriting and access of any class at runtime. <br> *(Requires 'tools.jar' in classpath)* |
| Obfuscator | Obfuscator offering renaming _(classes / fields / method / local variables)_ and string encryption.  |
| Scan       | Searches for class/method usage that can be used in a potentially malicous manner. |
| Visualizer | A tool used for finding object usage in a program. It can decompile classes using ASM & procyon. |

## Screenshots

* [Skidfuscator](screenshots/s1.png)
* [SkidScan](screenshots/s2.png)
* [SkidMapper](screenshots/s3.png)
* [SkidVisualizer](screenshots/s4.png)
