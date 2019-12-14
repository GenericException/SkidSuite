## Crash

The anti-tamper feature crashes in certain cases.

```
me.itzsomebody.radon.exceptions.RadonException: Constant pool size miscalculation in <CLASS-NAME>
        at me.itzsomebody.radon.transformers.obfuscators.AntiTamper.lambda$transform$8(AntiTamper.java:88)
        at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(Unknown Source)
        at java.util.stream.ReferencePipeline$2$1.accept(Unknown Source)
        at java.util.HashMap$ValueSpliterator.forEachRemaining(Unknown Source)
        at java.util.stream.AbstractPipeline.copyInto(Unknown Source)
        at java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
        at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(Unknown Source)
        at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(Unknown Source)
        at java.util.stream.AbstractPipeline.evaluate(Unknown Source)
        at java.util.stream.ReferencePipeline.forEach(Unknown Source)
        at me.itzsomebody.radon.transformers.obfuscators.AntiTamper.transform(AntiTamper.java:53)
        at me.itzsomebody.radon.Radon.lambda$run$1(Radon.java:104)
```

The following crash happens for the given classes in the sample jar:

 - sample/string/StringsDummyApp
 - sample/inheritance/Linear$A
 - sample/generics/GenericsMisc
 - sample/string/StringsDuplicates
 - sample/stream/StreamMisc
