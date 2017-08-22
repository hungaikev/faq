### STREAMS

A stream is a sequence of data elements made available over time. A stream can be thought of as items on a conveyor belt
being processed one at a time rather than in large batches.
 
A stream represents a sequence of objects (usually bytes, but not necessarily so), which can be accessed in sequential order

It is best to think of a stream in terms of the operators it offers (which vary from implementation to implementation, 
but have a lot in common)

Stream processing lets us model systems that have state without ever using assignment or mutable data. 

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


A particular stream might support reading(in which case it is an "Input Stream") writing ("Output Stream") or both. 

Stream is a useful abstraction because it can describe files(which are really arrays) but also input/output sockets,
serial ports etc

There are files streams in which you open a file and can read the stream or write to it. 
There are network streams where reading from and writing to the stream reads from and writes to an underlying established network connection. 


Another common thing you might find is textual streams that allow you to write strings instead of bytes


Anything can be a stream, variables, user input, properties, caches, data structures. 

Most common functions do ```combine```, ```create``` and ```filter``` any of these streams

1. A stream ca be used as an input to another one
2. Multiple streams can be used as inputs to another stream
3. You can merge two streams. 
4. You can filter a stream to get another one that has only events you are interested in. 
5. You can map data values from one stream to another new one. 


A stream is a sequence of ongoing events ordered in time. 
It can emit 3 different things; a value of some type, an error or a completed signal. 


