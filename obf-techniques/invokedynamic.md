# InvokeDynamic

Java has an instruction called `InvokeDynamic`. Sounds fancy, but its actually pretty simple once its explained. Here's an example of a Java program that uses it. All it does is print `"hi"`.

```java
public class Demo {
	public static void main(String[] args) {
		exec(Demo::example);
	}

	// the code we want to run
	static void example() {
		System.out.println("hi");
	}

	// runs a runnable
	static void exec(Runnable r) {
		r.run();
	}
}
```

The `exec` method takes a `Runnable` interface, which just defines `void run()` and calls it.

The `example` method prints hi.

The `main` method calls exec, but uses a lambda notation to point to the `example` function. 

So what's going on with that? The compiler knows that `Runnable.run()` has the same arguments and return type of `void example()` in our class, so we can use this lambda notation to essentially map our defined `example()` to `Runnable.run()`. The compiler will use `InvokeDynamic` to resolve a reference to our `example()` method and then wrap it in a instance of `Runnable` that is generated at runtime. This value is then stored in a `CallSite` implementation, usually `ConstantCallSite`, and any following time we run into this instruction the JVM will just pull up that `CallSite` and invoke whatever it contains. So it only needs to be resolved once before it behaves like any other normal `InvokeX` instruction on the resolved content.

Going into more depth this is the disassembly of the class:

```java
public Demo();
  0: aload_0
  1: invokespecial #1     // Method java/lang/Object."<init>":()V
  4: return

public static void main(java.lang.String[]);
  0: invokedynamic #2  0   // InvokeDynamic #0:run:()Ljava/lang/Runnable;
  5: invokestatic  #3      // Method exec:(Ljava/lang/Runnable;)V
  8: return

static void example();
  0: getstatic     #4      // Field java/lang/System.out:Ljava/io/PrintStream;
  3: ldc           #5      // String hi
  5: invokevirtual #6      // Method java/io/PrintStream.println:(Ljava/lang/String;)V
  8: return

static void exec(java.lang.Runnable);
  0: aload_0
  1: invokeinterface #7  1  // InterfaceMethod java/lang/Runnable.run:()V
  6: return
```

Quite simple. Now, you see how the `invokedynamic #2  0` has that `#2` part in it? That is the constant pool index of an `InvokeDynamic` entry. These pool entries have an index pointing to the `BootstrapMethodAttribute` index, and another constant pool index of a `NameAndType` entry defining the name of the method of the interface type intended to be wrapped and a getter signature of the interface type. In our case the `NameAndType` is `run:()Ljava/lang/Runnable;`. Next is the `BootstrapMethodAttribute` which points to `#0` in our case which is this beautiful blob:

```java
BootstrapMethods:
  0: #30 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #31 ()V
      #32 invokestatic Demo.example:()V
      #31 ()V
```

Looks scary, but lets break it down. The `#30 invokestatic blablabla` is a method reference to the standard `LambdaMetafactory` callsite generator. For any bootstrap method they will always follow the given signature pattern:

```java
	public static ConstantCallSite customLookup(
			MethodHandles.Lookup lookup, 
			String callerName,
			MethodType callerType, // any argument after this is not required, and is implementation specific
			int key // for example, this arg 'key' is not required (see explaination below)
    ) { ... }
```

As per the comment, only the first 3 arguments are required. Any additional argument past the `callerType` is not implicitly required by the JVM spec. The first 3 arguments are populated automatically by the JVM when you execute a `invokedynamic` instruction. So why does `LambdaMetafactory.metafactory` have 3 additional arguments? Each argument past the 3rd is mapped to the type that is stored as a constant argument in the `BootstrapMethodAttribute` entry. In the `metafactory` case:

1. MethodType samMethodType = `()V`
2. MethodHandle implMethod = `invokestatic Demo.run:()V`
3. MethodType instantiatedMethodType = `()V`

See how the arguments in the bytecode line up to the definition in `LambdaMetafactory.metafactory`? These details allow the `metafactory` implementation to know how to look up and define the custom `Runnable` instance from our `Demo` that just calls `example()`. 

For more information on how Java does this you can check out [LambdaMetafactory](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/LambdaMetafactory.html). This concludes how the `javac` compiler uses `InvokeDynamic`.

***

Now remember how I said the first 3 args are the only required arguments? Now we can consider _custom `CallSite` lookups!_ Since we know how `metafactory` roughly works, what if we supplied different arguments in the `BootstrapMethodAttribute`? We can easily put an `int` in the args and have our `customLookup` function map that key to a `MethodHandle` and wrap it in a `ConstantCallSite`. Because this is not something `javac` would normally output the average decompiler will have no way to really know what the `int` argument means. So if we had a system where we mapped `int` to some logic that does something similar to `metafactory` where we generate a new `Runnable` pointing to the `void example()` function you can break the decompiler's ability to display lambda functions.

Your lookup doesn't need to be that complicated, as long as its just different decompilers do not have the pattern recognition ability to understand you're invoking a lambda.

Additionally who ever said we're limited to invoking lambdas? The `invokedynamic` instruction essentially is just a lazy load operation for method handles, so who's to say we can't map an entire program's normal `invokevirtual` and `invokestatic` calls to `invokedynamic`? Well, fun fact you can! And much more. Dig into method handles, its a real rabbit hole.

So to summarize, you can use non-metafactory bootstrap methods in `invokedynamic` instructions to break recognition of lambdas, and also hide your method calls in a level of indirection. Anyone with the time can look at a bootstrap method and reverse engineer it to get the original operation back if you were to go the indirection route, but its good at scaring away noobs who don't know how to write transformers.