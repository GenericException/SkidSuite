# Radon Samples

**Versions used**: 

* 1.0.3 - October 3rd, 2018
* 2.0.0 - November 14th, 2019

### Legend - 1.0.3

Sample names are based on the given table of options:

| Abbreviated | Full Option |
| ------------| ------------|
| Str1    | String Encryption: Light   |
| Str2    | String Encryption: Medium  |
| Str3    | String Encryption: Heavy   |
| Indy1   | Invoke Dynamic: Light      |
| Indy2   | Invoke Dynamic: Medium     |
| Indy3   | Invoke Dynamic: Heavy      |
| Flow1   | Flow Obfuscation: Light    |
| Flow2   | Flow Obfuscation: Medium   |
| Flow3   | Flow Obfuscation: Heavy    |
| Num1    | Number Obfuscation: Light  |
| Num2    | Number Obfuscation: Medium |
| Num3    | Number Obfuscation: Heavy  |
| Crasher | Crasher |

### Legend - 2.0.0

Sample names are based on the given table of options:

| Abbriviated | Full Option / Description |
| ------------| ------------|
| Str             | String Encryption: Base   |
| StrContext      | String Encryption: Base + Context Checks  |
| StrPool         | String Encryption: Base + Pooling   |
| AntiTamper      | Prevent tempering with the jar, affects strings  |
| IndyFast        | Invoke Dynamic: Fast      |
| IndySlow        | Invoke Dynamic: Slow      |
| FlowGoto        | Flow Obfuscation: Replacing GOTO instructions with others |
| FlowBogusSwitch | Flow Obfuscation: Inserting fake switch blocks |
| FlowBogusJump   | Flow Obfuscation: Insert opaque predicates |
| FlowSplitBlocks | Flow Obfuscation: Splits blocks in half multple times |
| FlowFakeCatch   | Flow Obfuscation: Insert fake try-catch blocks |
| FlowUglyNull    | Flow Obfuscation: Replace IF_ACMP<EQ/NE> with try-catch blocks |
| BadAnno         | Apply crash-inducing annotations to all members |
| Virtualize      | Use a custom instruction set to hide methods in a minimal Virtual Machine |
| Eject           | Obscure method/field references with specialized outlining |
| EjectArg        | Eject + adding dummy arguments |