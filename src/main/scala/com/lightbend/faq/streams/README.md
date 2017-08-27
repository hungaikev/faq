### STREAMS

A stream is a sequence of data elements made available over time. A stream can be thought of as items on a conveyor belt
being processed one at a time rather than in large batches.

Streams are sequences of data, divided up into individual elements. 

The size of a stream may not be known or may be infinite. 

Often streams are too large to fit in memory. 
 
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


### Typical operations on a stream: 

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


### Examples: 

1. Twitter "firehose" of Tweets
2. Live video/audio streams 
3. Data from a fitness tracker


### Back Pressure 

Implemented using pull/push mechanism. 

Subscribers signal demand. Demand is sent upstream via subscription.

Publishers receive demand and push data (if available) downstream. 

Publishers are forbidden from pushing more than the demand. 


### Akka streams relationship to Actors 

Actors consume streams of data in the form of messages. 

It can be tedious and error prone to implement streams with back pressure between actors manually. 

Akka Streams provides a higher level api for stream processing, backed by akka actors.
 
Akka streams provide statically typed guarantees that prevent wiring errors. 


### What is an Akka Stream 

Data flows through a chain of processing stages. 

Stages consist of zero or more inputs and zero or more outputs. 

Stages must have at least one input or output. 

By default stages are fused together to run synchronously inside a single actor but can be mad to run asynchronously 
in separate actors. 


### Linear Streams 

 File ~>  Source.fromFile ~> Flow.map(transform) ~> Sink.foreach ~> Database
 
 1. **Sources** - The ```source``` of the data in the stream 
 2. **Sinks** - The ```destination``` for the data in the stream. 
 3. **Flows** - Transformations to the data in the stream. 
 4. **Runnable Graphs** - A stream where all the inputs and outputs are connected. 
 
 Each stage in the stream can be executed synchronously or asynchronously. 
 
 In most cases, element order is preserved. 
 
 Back pressure is propagated from downstream stages to upstream. 
 
 Linear streams are often sufficient for most use cases. 
 
 
 ### Graphs 
 
 Source ~> Junction(fan in)  ~> Junction(fan in) ~> Sink 
 Source ~>                   ~> 
 Source ~> Junction(fan out) ~> Flow ~> Sink 
 
 
**Junctions** - Branch points in the stream (eg ```fan in```, ```fan out```)

Graphs allow us to build complex flows of data with multiple inputs and outputs. 


### Graph Stages are Templates 

**Sources/Flows/Sinks/Junctions** are immutable re-usable templates. They contain instructions on how to produce/transform/consume data.
By themselves they do nothing. In order to start the flow of data the graph must first be materialized. 

### Materialization

Materialization is the act of allocating resources to the stream. It occurs when all stages in the stream are connected and the stream is run. 

Running the stream results in Materialized Values being produced. 

Materialized values and their type originate i a Source and are propagated through all stages of a stream to a sink.  

Each stage is capable of producing a single materialized value. 

Materialized values are separate from the elements being **produced/transformed/consumed/ ** by the stage. 

An implicit materializer is required for the graph to run. 

```NotUsed``` indicates that the materialized value is not important in this stage. It is a type signature for Akka Streams. 

To understand the meaning of ```NotUsed``` we need to understand that akka streams are hybrid beasts that have 2 different concepts of 
value 

1. The value of what ```flows``` in the stream
2. The value of what is produced and visible outside of the stream. 

An akka stream is only going to run if it is closed, which is to say that it has a beginning and an end and therefore nothing outside the flow
can peek into it. 

Akka Streams allows to materialize (or keep) either the **left** or the **right** value of a stream.

Akka streams are back pressured by default, but it is possible to alter this behaviour. 

```Kill Switches``` - This is an object used externally to stop the materialization of a stream. 










