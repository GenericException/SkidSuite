# Radon Samples

**Versions used**: 

* 1.0.3 - October 3rd, 2018
* 2.0.0 - November 14th, 2019

### Legend - 1.0.3

Sample names are based on the given table of options:

| Sample Name | Full Option |
| ------------| ------------|
| string1    | String Encryption: Light   |
| string2    | String Encryption: Medium  |
| string3    | String Encryption: Heavy   |
| indy1      | Invoke Dynamic: Light      |
| indy2      | Invoke Dynamic: Medium     |
| indy3      | Invoke Dynamic: Heavy      |
| flow1      | Flow Obfuscation: Light    |
| flow2      | Flow Obfuscation: Medium   |
| flow3      | Flow Obfuscation: Heavy    |
| num1       | Number Obfuscation: Light  |
| num2       | Number Obfuscation: Medium |
| num3       | Number Obfuscation: Heavy  |
| crasher    | Crasher |

### Legend - 2.0.0

Sample names are based on the given table of options:

| Sample Name | Full Option / Description |
| ------------| ------------|
| string           | String Encryption: Base   |
| string-context   | String Encryption: Base + Context Checks  |
| string-pool      | String Encryption: Base + Pooling   |
| antitamper       | Prevent tempering with the jar, affects strings  |
| indy-fast        | Invoke Dynamic: Fast      |
| indy-slow        | Invoke Dynamic: Slow      |
| flow-goto        | Flow Obfuscation: Replacing GOTO instructions with others |
| flow-bogusswitch | Flow Obfuscation: Inserting fake switch blocks |
| flow-bogusjump   | Flow Obfuscation: Insert opaque predicates |
| flow-splitblocks | Flow Obfuscation: Splits blocks in half multple times |
| flow-fakecatch   | Flow Obfuscation: Insert fake try-catch blocks |
| flow-uglynull    | Flow Obfuscation: Replace IF_ACMP<EQ/NE> with try-catch blocks |
| bad-anno         | Apply crash-inducing annotations to all members |
| virtualize       | Use a custom instruction set to hide methods in a minimal Virtual Machine |
| eject            | Obscure method/field references with specialized outlining |
| eject-arg        | Eject + adding dummy arguments |