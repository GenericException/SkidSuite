# Scuti Samples

**Versions used**: 

* 0.0.1 - May 18th, 2020

### Legend - 0.0.1

Sample names are based on the given table of options:

| Sample Name | Full Option / Description |
| ------------| ------------|
| anno           | Invalid annotations   |
| class-encrypt  | Encrypt class files. StringKey=`1337`, ClassKey=`69`  |
| corrupt-names  | Corrupt class names _(Also known as fake-directory)_ |
| corrupt-stream | Corrupt output stream |
| flow           | Flow Obfuscation: Base   |
| flow-goto      | Flow Obfuscation: Base + Goto flooding   |
| flow-try       | Flow Obfuscation: Base + Heavy try catch   |
| flow-trygoto   | Flow Obfuscation: Base + Heavy try catch + Goto flooding   |
| hide-code      | Hide code   |
| indy           | Invoke dynamic   |
| num            | Number obfuscations  |
| num2           | Number obfuscations, twice enabled  |
| mass-sig       | Massiv signature |
| push-transient | Push transient  |
| push-varargs   | Push varargs  |
| rand-err       | Random exceptions |
| string-fast    | String Obfuscation: Fast   |
| string-strong  | String Obfuscation: Strong   |
| dup-var        | Duplicate Variables |
| var-desc       | Randomize variable descriptors  |

### Notes - 0.0.1

Flow and invokedynamic obfuscations typically generate classes/fields with really long names.
Since the names do not matter much aside from lagging text-based output, I've renamed these long names.