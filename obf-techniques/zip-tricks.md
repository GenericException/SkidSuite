# Fake Directory

Zip tools expect directories to end with `/`, but a file entry with data can also end with a trailing `/`. There is no rule against it, but its so obvious in most cases that files don't do that... until they do. 

Since most tools treat paths ending in `/` as directories without checking for data, you can hide application logic as _"folders"_. The JVM will happily load `Hello.class/` because it explicitly checks for a trailing `/` and loads the content if  it contains a class file.

More information over at: [x4e/fakedirectory](https://github.com/x4e/fakedirectory) 

# Fake Header

Opening a Jar file in a hex editor will show you that the file header is the standard Zip `50 4B 03 04` / `PK..`. Most Zip parsers expect this header to be at the immediate start of the file. But the JVM lets you put data before there. Inserting some random junk bytes before the file header will break lots of programs expecting zip file inputs, but the JVM seems to seek to the start of the proper header in order to load from jar files.