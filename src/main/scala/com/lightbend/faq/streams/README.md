### STREAMS

A stream is a sequence of data elements made available over time. A stream can be thought of as items on a conveyor belt
being processed one at a time rather than in large batches.
 
A stream represents a sequence of objects (usually bytes, but not necessarily so), which can be accessed in sequential order

Streams are processed differently from batch data, normal functions cannot operate on streams as whole as 
they have potentially unlimited data and formally streams are codata(potentially unlimited) 

Functions that operate on a stream producing another stream are know as filters and can be connected in pipelines, 
analogously to function composition. Filters may operate on one item of a stream at a time, or may base an item of 
output on multiple items of input such as a moving average. 

On Unix and related systems based on the C language, a stream is a source or sink of data, usually individual bytes of 
characters.

Streams are an abstraction used when reading or writing files, or communicating over network sockets. 

A byte stream is a sequence of bytes. 


Typical operations on a stream: 

1. Read one byte. Next time you read, you will get the next byte and so on. 
2. Read several bytes from the stream into an array/list
3. Seek(move your current position in the stream so that next time you read you get bytes from the new position)
4. Write one byte
5. Write several bytes from an array/list into the stream
6. Skip bytes from the stream (this is like read, but you ignore the data)
7. Push back bytes into an input stream

