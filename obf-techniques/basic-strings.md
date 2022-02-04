# Strings

When a reverse engineer with nefarious intent is looking at bypassing some software protections where do they start their search? Well, there are plenty of ways to dive into a program but one of the easiest is a simple string search. Even a basic hex-editor or even notepad++ search can show which class files have a string in them _(Assuming its long and unique enough to not match garbage)_. With such an obvious landmark how can you hide something like `Invalid login` or `License expired!` in a Java application?

Well, I've seen plenty of different ways so here are a few worth noting. I will be using the following example for these:

```java
void run() {
	if (isValidLicense()) {
		open();
	} else {
		println("Invalid license!");
	}
}
```

**Manual** - Too lazy to use an obfuscator? Just make your own source code harder to understand!

```java
void run() {
	if (isValidLicense()) {
		open();
	} else {
		println(new StringBuilder().append("In").append("va").append("li").append("d l").append("ic").append("en").append("se!").toString());
	}
}
```

**Xor/Rot13** - The most simple string cipher. Prevents basic searches. Once found, attackers can dump all strings then run them back throgh xor/rot13 to get all the original strings back. If using Xor, best to use a different key for each string to prevent this. Still weak in general.

```java
void run() {
	if (isValidLicense()) {
		open();
	} else {
		println(rot13("Vainyvq Yvprafr!"));
	}
}
```

**Modified Xor** - As long as its something a little bit different from a flat xor, you can't literally just google `"xor cipher online"` and decrypt the strings. Maybe each index in the string affects the xor key? Maybe there is a mapping of xor keys to each index in the string? Adding in handling for non-alphabet characters would be a plus too.

```java
void run() {
	if (isValidLicense()) {
		open();
	} else {
		println(customXor("Zgbyzruh+Agayxhw]"));
	}
}
```

**Byte Array** - You can create strings from byte arrays using `new String(array)`. If you have an obfuscator that automatically transforms string constants to an a byte array and passes it to a `String` constructor there won't be any string constants to search through at all. It may be noticeable in a hex viewer given the padding between each letter for instructions handling array management. Now, an attacker can literally just copy-paste the `new String(byte-array-here)` to get the text, but its a neat trick to layer on top of another technique, so long as the string is only visited once by the program control flow. Doing this in a tight loop would be terrible for performance.

```java
void run() {
	if (isValidLicense()) {
		open();
	} else {
		println(rot13(new String(new byte[] {86, 97, 105, 110, 121, 118, 113, 32, 89, 118, 112, 114, 97, 102, 114, 33})));
	}
}
```

**AES/Key decryption with runtime values** - A neat trick is to use runtime values to generate the decryption key for some encrypted text. For instance, you can use `new Throwable().getStackTrace()` to access the call stack. Implementing the string decryption to pull the name of the method that defines the string `stackTop - 1` is fairly easy to implement. From here onwards it just makes sense for an attacker to write an automated transformer to dump strings instead of doing it by hand.

```java
void run() {
	if (isValidLicense()) {
		open();
	} else {
		println(aes("5xfpwLGilOgdFMxTGh6tDxlU1yBVun2zzxQvQYyODrg="));
	}
}

String aes(String in) {
    String name = new Throwable().getStackTrace()[1].getMethodName();
    String key = createKey(name);
    // aes decrypt code here
    return ...
}
```
