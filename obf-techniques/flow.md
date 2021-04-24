# Flow Control

Flow control is essentially the jump/switch/for/while/etc logic that manipulates the _flow_ of your program. Obfuscators can make simple if and for loops into much more complex patterns that confuse decompilers and/or make it a pain to follow at the bytecode level.

## Demo

**Original**: Just a standard hello world with two possible flow paths.

```java
void hello(String name) {
    if (name == null)
        println("Cant say hello to nobody!");
    else
    	println(name);
}
```

**Opaque Predicates**: Same logic, but with extra conditionals and behavior added. They are generated in such a way to always evaluate a certain way, they just pollute the intent of the existing logic with unnecessary steps. 

Example with extra basic conditional checks.

```java
void hello(String name) {
    int temp = 0;
    if (temp != 0)
        throw new Exception(); // Never thrown, 'temp != 0' is always 'false'
    else if (temp < 1 && name == null)
        println("Cant say hello to nobody!");
    else if (temp > -1)
    	println(name);
    else
        throw new Exception(); // Never thrown, one of the two cases above will always be the flow path followed
}
```

Example with `switch` statements.

```java
void hello(String name) {
    int temp = 0;
    switch(temp) {
        case 0:
            break;
        case 1:
            return; // never reached, control flow hits the 'break' below and continues normally
    }
    if (name == null) {
        switch(temp) {
            case 0:
                println("Cant say hello to nobody!");
                break;
            default:
                throw new Exception(); // never thrown, temp == 0 so the print is called
        }
    } else {
        do {
            println(name);
        } while(temp > 0) // because this is a do-while, it will eval once, but the check is always 'false' so it won't run again
    }
}
```

Example with intentionally throwing exceptions in `try` blocks, sort of like using it as a uglier `goto` where the destination is the exception handler block. 

```java
void hello(String name) {
    try {
        String temp = null;
        temp.toString();
    } catch (NullPointerException ex) {
        // We intentionally cause a NPE above to move the control flow to this handler block
        if (name == null)
            println("Cant say hello to nobody!");
        else
            println(name);
    }
}
```

