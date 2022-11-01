# Obfuscators

## Free / Open Source

| Tool  | Description  | Link |
|-------|--------------|------|
| **Bozar** | An open source Java obfuscator. | [Source](https://github.com/vimasig/Bozar) |
| **Caesium** | An open source Java obfuscator by sim0n. Offers some neat ZIP file tricks _(Auto-patchable in Recaf)_ among other common JVM obfuscation features. | [Source](https://github.com/sim0n/Caesium) |
| **dProtect** | dProtect is an extension of ProGuard adding simple string/flow/arithmetic obfuscation. | [Source](https://github.com/open-obfuscator/dProtect) |
| **JObf** | JObf is an open source obfuscator by superblaubeere27. | [Source](https://github.com/superblaubeere27/obfuscator) |
| **Proguard** | Proguard is one of the most common obfuscators out there. It's one of the oldest and most imporantly, its totally free. It really only supports name obfuscation and not much else. Ironically, you can use it on obfuscated jars to weaken name obfuscation into something more workable. | [Proguard](https://www.guardsquare.com/en/products/proguard) - [Source](https://github.com/Guardsquare/proguard) |
| **Radon** | **DISCONTINUED** Radon is an open source obfuscator by ItzSomebody. | [Source](https://github.com/ItzSomebody/Radon) |
| **Scuti** | Scuti is an open source obfuscator by netindev. | [Source](https://github.com/netindev/scuti) |
| **yGuard** | yGuard is similiar to proguard in that its more of a code-shrinker than of an obfuscator. | [yGuard](https://www.yworks.com/products/yguard) - [Source](https://github.com/yWorks/yGuard) |

## Premium

| Tool  | Description  | Price | Link |
|-------|--------------|-------|------|
| **Allatori** | Allatori is has a free demo with basic obfuscation features. You can't beat free. | <ul><li>Demo: Free</li><li>Full: $290+</li></ul> | [Allatori](http://www.allatori.com/) |
| **Binscure** | **DISCONTINUED** Binscure offers standard obfuscation features _(Except renaming)_ plus decompiler/ASM crashing capabilities _(Auto-patchable in Recaf)_. Flow control crashes most decompilers. | <ul><li>~~Full: 75£+ _(Crypto only)_~~</li><li>Free (development ended)</li></ul> | [Binscure](obf/Binscure/INDEX.md) |
| **Branchlock** | A web based obfuscator for Java and Android. Offers standard obfuscation features plus decompiler crashing tools  _(Auto-patchable in Recaf)_ | <ul><li>Full: €64+</li></ul> | [Branchlock](https://branchlock.net/) |
| **DashO** | DashO is a step above Allatori in almost every department. The flow obfuscation is more noticeable than in some other obfuscators, the string encryption is more dynamic, and it has more features overall. | <ul><li>Trial: 1 Week </li><li>Full: Varies, [~$2000](https://www2.cs.arizona.edu/~collberg/Teaching/620/2008/Assignments/tools/DashO/) </li></ul> | [DashO](https://www.preemptive.com/products/dasho/overview) |
| **Dexguard** | Dexguard is an Android application obfuscator, supports all the common features plus a few extra Android-specific abilities.  | <ul><li>Full: Varies</li></ul> | [Dexguard](https://www.guardsquare.com/en/products/dexguard) |
| **DexProtector** | DexProtector is another Android application obfuscator made by Licel (developers of Stringer). | <ul><li>Demo: Requires company verification</li><li>Full: Varies</li></ul> | [DexProtector](https://dexprotector.com/) |
| **ESkid** | **DISCONTINUED** Similar to binscure/paramorphism in terms of features. Offers decompiler/ASM crashers _(Auto-patchable in Recaf)_ | <ul><li>Full: $75+</li></ul> | [ESkid](https://eskid.eridani.club/) |
| **Excelsior JET** | **DISCONTINUED** Technically not an obfuscator, as it is actually an ahead-of-time compiler. Still, reversing native code is effectively obfuscation in comparison to reversing java code.  | <ul><li>Full: $3,000</li><li>Personal: Conditionally free</li></ul> | [Excelsior JET](https://www.excelsiorjet.com) |
| **Jfuscator** | Aside from the fact that it can't support anything past Java 7, its about on par with DashO. String obfuscation is dynamic enough to basic skids. Flow obfuscation works, but isn't that strong. | <ul><li>Demo: Free 2 weeks</li><li>Full: $599 + _(optional $195 support)_</li></ul> | [Jfuscator](https://secureteam.net/jfuscator) |
| **JNIC** | Maps method bodies to JNI generated code that is bundled with your jar. Useful to deter skids who can't open IDA and map out JNI structs. | <ul><li>Full: Varies _(£150+)_</li></ul> | [jnic](https://jnic.dev/) |
| **Paramorphism** | Paramorphism utilizes ZIP file corruption to deter usage of common mainstream reverse-engineering tools. There's also a [discord](https://discord.gg/k9DPvEy) chat. | <ul><li>Full: $80</li></ul> | [Paramorphism](https://paramorphism.dev/) |
| **Stringer** | Stringer is a relatively strong obfuscator. The flow obfuscation on high settings is painful to reverse engineer _(Requires minor emulation)_. It boasts some other neat features others don't have like resource protection and reflection obfuscation. Sadly the cooler features are locked away under the enterprise license which I cannot find any information about online. Just assume it costs an arm and a leg. | <ul><li>Demo: Requires company verification</li><li>Non-Enterprise: $3,000+</li><li>Enterprise: Varies</li></ul> | [Stringer](https://jfxstore.com/stringer/) |
| **Zelix Klassmaster** | Zelix is another great obfuscator. It is an oldie, and their website shows it. Despite the outdated website Zelix is consistently updated and is battle-tested. For the features it gives the smallest license is an absolute steal. | <ul><li>Demo: Free, requires company email</li><li>Full: $240+</li></ul> | [Zelix Klassmaster](https://www.zelix.com/klassmaster/index.html) |

> **Note**: _"Varies"_ means that the price is not set in stone and is determined when you ask for a quote through their sales team. 

> [_(Back to README)_](README.md)
