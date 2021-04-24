# Scuti Samples

**Versions used**: 

* 0.0.1 - May 18th, 2020

### Legend - 0.0.1

Sample names are based on the given table of options:

| Abbreviated | Full Option / Description |
| ------------| ------------|
| Anno           | Invalid annotations   |
| ClassEncrypt   | Encrypt class files. StringKey=`1337`, ClassKey=`69`  |
| CorruptNames   | Corrupt class names _(Also known as fake-directory)_ |
| CorruptStream  | Corrupt output stream |
| Flow           | Flow Obfuscation: Base   |
| FlowGoto       | Flow Obfuscation: Base + Goto flooding   |
| FlowTry        | Flow Obfuscation: Base + Heavy try catch   |
| FlowTryGoto    | Flow Obfuscation: Base + Heavy try catch + Goto flooding   |
| HideCode       | Hide code   |
| Indy           | Invoke dynamic   |
| Num            | Number obfuscations  |
| Num2           | Number obfuscations, twice enabled  |
| PushTrans      | Push transient  |
| PushVarargs    | Push varargs  |
| RandErr        | Random exceptions |
| StrFast        | String Obfuscation: Fast   |
| StrStrong      | String Obfuscation: Strong   |
| VarDesc        | Randomize variable descriptors  |

### Notes - 0.0.1

Flow and invokedynamic obfuscations typically generate classes/fields with really long names.
Since the names do not matter much aside from lagging text-based output, I've renamed these long names.