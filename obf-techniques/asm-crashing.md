# ASM Crashing

So how does it work? ASM authors pride it on being a fast library. And you know what is _"slow"?_ Input validation and proper error handling. This is usually fine since `javac` generates classes that perfectly match the specification, which ASM follows to a T. But if you have attributes that normally have 1 or more items _(Exception table entries for example)_ suddenly being ignored because the content length is set to 0 _(implying there will be no items)_ then ASM chokes because `javac` would _never_  emit code like this. If you open a ticket about this behavior they will close your issue for being out of scope.

Another example, JVM lazily loading information can allow unreferenced items to be malformed. The primary culprit in this case is annotations. If you annotate a field that is never referenced, the JVM will never parse the contents of that annotation. So what if our annotation states that its type index is in the constant pool at index 9999999? Well, at runtime we don't care. But since ASM needs to parse the entire class it will try to access an internal byte array without any index checking and throw an `ArrayIndexOutOfBoundsException`. Apply this logic to any attribute that can be loaded lazily by the JVM.

Here's a snippet from ASM to showcase what I mean by internal byte array access:

```java
public String readUTF8(final int offset, final char[] charBuffer) {
  // the cp index will be something dumb like 999_999, but the cp may only have 1_000 or so items
  int constantPoolEntryIndex = readUnsignedShort(offset); 
  if (offset == 0 || constantPoolEntryIndex == 0) 
    return null;
  // 'readUtf' will do buffer[constantPoolEntryIndex] and crash
  return readUtf(constantPoolEntryIndex, charBuffer); 
}
```

Thankfully tools exist to automatically patch away this behavior:

* [CAFED00D](https://github.com/Col-E/CAFED00D)
* [Recaf](https://github.com/Col-E/Recaf)

