# Branchlock _(3.0.0)_

Branchlock is an online commercial obfuscator. It advertises itself as _"modern, lightweight, but powerful"_.

## Anti-Reversing tricks

### 1: Abusing Java Swing's HTML rendering

Swing UI's are often customized by using HTML elements such as `<b>` and `<span style="color: red">`. Branchlock inserts an `<img>` tag into a class's name _(which is illegal and won't run due to path constraints, but is 'valid' enough for the class file spec to be parsable)_ so that Java reversing tools made with Swing that do not explicitly disable this feature advertise the product.

![img](docs/swing.png)

This does not affect applications that have disabled this feature or use a different UI framework.

![img](docs/contents.png)

### 2. Class inheritance infinite loop

![img](docs/hierarchy.png)

Branchlock dumps an empty class that `extends` itself so that if you run any automated tools that try to create class hierarchies they will get stuck in an infinite loop. AFAIK Recaf is the primary target here since it uses JPhantom to generate missing classes to assist its recompilation feature, and this _used_ to kill Recaf but [has since been patched](https://github.com/Col-E/Recaf/commit/53da1d3adf0e831e89f5f580c1bf4e08d81c9a6c).

### 3. Long path

This occurs in multiple locations. Its a common trick that can make UI based tools choke when they suddenly need to generate 1000's of graphic nodes of directory entries that are effectively useless. Again, [Recaf has addressed this by flattening long paths](https://github.com/Col-E/Recaf/commit/07a4b37d99514a5fab4830583b4fbb810e2d42f9).

![img](docs/fakedir.png)

![img](docs/hidden-meta.png)

![img](docs/long-path.png)

### 4. FakeDirectory

This trick abuses an edge case in how the JVM reads classes from zip files vs how most standard ZIP based tools function. You can have a zip data entry path end in a forward slash and the JVM will still check the entry for a class if the name otherwise ends in `.class`

Branchlock applies this to all classes.

![img](docs/fakedir2.png)