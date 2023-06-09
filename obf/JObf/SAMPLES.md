# JObf Samples

**Versions used**: 

* 1.9.3 - March 15th, 2019

### Legend - 1.9.3

Sample names are based on the given table of options:

| Sample Name | Description |
| ------------| ------------|
| crash-anno-spam        | Crasher with annotation spam |            
| crash-invalid-sigs     | Crasher with invalid generic signatures |               
| flow-all               | Flow control obfuscation, all settings enabled |     
| flow-bad-concat        | Flow control obfuscation, only bad-concat |            
| flow-bad-pop           | Flow control obfuscation, only bad-pop |         
| flow-cmp               | Flow control obfuscation, only comparison mangling |     
| flow-mangle-local-vars | Flow control obfuscation, only local variable mangling |                   
| flow-mangle-return     | Flow control obfuscation, only return mangling |               
| flow-mangle-switch     | Flow control obfuscation, only switch mangling |               
| flow-replace-goto      | Flow control obfuscation, only goto replacement |              
| flow-replace-if        | Flow control obfuscation, only if replacement |            
| hwid                   | HWID protection enabled for `my-hwid-is-awesome` | 
| indy                   | Invoke dynamic | 
| inline                 | Inliner |   
| num-all-without-array  | Number obfuscation, all settings enabled, except for array output (weakens obf) |                  
| num-all                | Number obfuscation, all settings enabled |    
| num-and                | Number obfuscation, with 'and' setting enabled |    
| num-ob-zero            | Number obfuscation, with 'obscure zero' setting enabled |        
| num-shift              | Number obfuscation, with 'shift' setting enabled |      
| num-to-array           | Number obfuscation, with 'to-array' setting enabled |         
| num                    | Number obfuscation, no settings enabled |
| reference-proxy        | Creates proxies to references |            
| string-aes             | String encryption, with 'aes' setting enabled |       
| string-hidden          | String encryption, with 'hide strings' setting enabled |          
| string                 | String encryption, no settings enabled |   
| everything             | All transformers enabled at max settings |