# Fake Directory

Zip tools expect directories to end with `/`, but a file entry with data can also end with a trailing `/`. There is no rule against it, but its so obvious in most cases that files don't do that... until they do. 

Since most tools treat paths ending in `/` as directories without checking for data, you can hide application logic as _"folders"_. The JVM will happily load `Hello.class/` because it explicitly checks for a trailing `/` and loads the content if  it contains a class file.

More information over at: [x4e/fakedirectory](https://github.com/x4e/fakedirectory) 

