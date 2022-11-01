# BisGuard _(Trial - 9.9)_

BisGuard is a commercial Java packer for the insane asking price of $1200. All it does is wrap your jar with a simple cipher. There is no additional protection.

***

BisGuard has no configuration because it only has the packing feature. I modified `obf-sample-test.jar` to point to the `StringsDummyApp` class as the main class in the `MANIFEST.MF` and used `key` as the cipher value. It overwrites the file you select as an input and copies the original to a `.bak` file in the same directory. In my case the now packed application lost its original `MANIFEST.MF`. This is apparently a bug because the I ran the application it complained that there was no information to pull from the `MANIFEST.MF`. The solution is not to make your own `MANIFEST.MF` but to either delete it or make it strictly with the `jar` command. If you choose to delete the file, BisGuard will offer suggested classes as the new main class.


```
java -jar output.jar
Nov 01, 2022 8:00:23 AM JavaLauncherStandalone main
SEVERE: Nov 01, 2022 8:00:22 AM JavaLauncherStandalone run
INFO: JavaLauncherStandalone STARTED
java.lang.Exception: Main class not found: sample.string.StringsDummyApp
        at JavaLauncherStandalone.run(JavaLauncherStandalone.java:260)
        at JavaLauncherStandalone.main(JavaLauncherStandalone.java:508)
Nov 01, 2022 8:00:23 AM JavaLauncherStandalone exitAction
INFO: JavaLauncherStandalone FINISHED
```

Very cool. Doesn't even run lmao. This app seems to be really buggy for the asking price of $1200. The problem was that the `MANIFEST.MF` was yet again missing data. I solved this by adding the jar name back to the empty attribute like so: `Encrypted-Jars: sample.jar`. For some reason it tried loading this from the current directory, so I copied the packed jar inside the launcher to the current directory. Then the application ran. Unfortunately the wrapped application was a command-line application and BisGuard doesn't work for those. You can't interact with the application at all.

## Unpacking

Go to the `JavaLauncherStandalone$Decoder` inner class. Modify `loadJars()` to add `Files.write(Paths.get(entryName), data);`

The bytecode to do so looks like:
```java
// Where the jar entry contents get dumped
ALOAD 18
INVOKEVIRTUAL java/io/ByteArrayOutputStream.toByteArray()[B
ASTORE 19

// Inserted logic
  // Get path for name
  ALOAD 17
  ICONST_0
  ANEWARRAY java/lang/String
  INVOKESTATIC java/nio/file/Paths.get(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;
  ASTORE 20
  // Create parent dirs
  ALOAD 20
  INVOKEINTERFACE java/nio/file/Path.getParent()Ljava/nio/file/Path;
  ICONST_0
  ANEWARRAY java/nio/file/attribute/FileAttribute
  INVOKESTATIC java/nio/file/Files.createDirectories(Ljava/nio/file/Path;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;
  POP
  // Write to location
  ALOAD 20
  ALOAD 19
  ICONST_0
  ANEWARRAY java/nio/file/OpenOption
  INVOKESTATIC java/nio/file/Files.write(Ljava/nio/file/Path;[B[Ljava/nio/file/OpenOption;)Ljava/nio/file/Path;
  POP

// Original map put call
ALOAD 11
ALOAD 17
ALOAD 19
INVOKEVIRTUAL java/util/HashMap.put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
POP
```
Then just run the app. The original contents of the jar will get dumped to the current directory.

## Official documentation

There is an [online 'guide'](https://www.bisguard.com/help/java/howitworks.html) explaining how BisGuard works _([Archive](https://archive.ph/9UaRI))_. Hilariously it shows the before/after of its "protection" by dropping the jar file into notepad... Yes, showing a binary file type in notepad.

It does mention there is a _"sonar"_ module to detect 'hacking' attempts but no such module appears in the trial. However if the quality of the full thing matches that of the trial I wouldn't be shocked to see these claims fall flat.