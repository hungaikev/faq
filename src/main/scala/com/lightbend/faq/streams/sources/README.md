## Sources 

```Source``` -  as the name suggests the source of the data, consists of exactly one output. 
```Source``` takes two type parameters. The first one represents the type of data it emits and the second one is the type of 
the auxiliary value it can produce when ran/materialized. If we do not produce any we use the ```NotUsed``` type provided by akka. 

Could be receiving data from a file, a database, a REST API, collection etc

Amount of data in the source is not predetermined. 

It may be infinite. 

A Source is defined as ```Source[+Out, +Mat]```
 1. Out defines the type of the elements that the source produces. 
 2. Mat defines the type of the materialized value.


Sources receive Demand from downstream. 

A Source can push data downstream as long as there is demand. 

If there is no demand, then the source is forbidden from pushing data. 

The source will have to deal with incoming data until demand resumes. 

### **Empty Sources**

```scala
val source: Source[String, NotUsed] = Source.empty[String]

```

```Source.empty``` 

* Creates an empty source of the specified type 
* Always completes the stream 
* Useful for testing what happens when you have an empty source. 



### **Sources from single elements**

```scala
val source: Source[String, NotUsed] = Source.single("Hello World")

val source2: Source[String, NotUsed] = Source.repeat("Hello World")

val source3: Source[String, Cancellable] = Source.tick (
   initialDelay = 1.second,
   interval = 5.seconds,
   tick = "Hello World"
)

```
```Source.single```
* Creates a Source with a single arbitrary element. 
* Push a single element and then complete. 

```Source.repeat```

* Similar to ```Source.single``` but the same element is infinitely pushed whenever there is demand. 

```Source.tick```
* Similar to ```Source.repeat``` except the element is pushed on a time schedule. 
* If there is no demand(i.e back pressure) no tick will be pushed. That tick will be lost. 

### **Sources from Iterables**

```scala
val source : Source[Int, NotUsed] = Source(1 to 10)

```
* Creates a Source from a ```collection.immutable.Iterable```
* Elements are taken from the Iterable and pushed downstream whenever there is demand. 
* The stream is completed if there is no more data in the Iterable. 

### **Sources from Iterators**

```scala
val source: Source[Int, NotUsed] = Source.fromIterator {
  () => Iterator.from(0)  //It goes on until integer max then goes to the negative
}


val source2: Source[Int, NotUsed] = Source.cycle {
 () => Iterator.range(1,10) //It goes on until 10 then starts all over again
}
```
```Source.fromIterator```
* Creates a ```Source``` from an Iterator 
* The Iterator is created each time the source is materialized. 
* Completes when ```hasNext``` returns false. 

```Source.cycle```
* Similar to ```Source.fromIterator```, but the iterator is infinitely repeated. 
* When ```hasNext``` return false, the Iterator is recreated and consumed again. 

### **Stateful Sources**

```scala
val countTo100: Source[Int, NotUsed] = Source.unfold(0) {
  case value if value <= 100 => Some((value + 1, value))
  case _ => None 
}
```
```Source.unfold```
* Uses an initial value and a transformation function. 
* The transformation function returns an Option of a tuple containing the value for the next Iteration and the value to push. 
* Completes when the transformation function returns None. 

```Source.unfoldAsync```
* Similar to unfold, but the function returns a Future of an Option

### **Sources from Actors**

```scala
case class Message(value: String)
val source: Source[Message, ActorRef] = Source.actorRef[Message] (
 bufferSize = 10, 
 overflowStrategy = OverflowStrategy.dropNew 
)
```
```Source.actorRef```
* Creates a Source that is materialized as an ActorRef
* Messages sent to the ActorRef will be pushed to the stream or buffered until there is demand. 
* Completes by sending the actor an ```akka.actor.status.Success``` or ```akka.actor.PoisonPill ```
* ```bufferSize``` - Determines the maximum capacity of the buffer. 
* ```overflowStrategy``` - Determines what to d if the buffer overflows. 

### **Sources from Files**
```scala
val byteSource: Source[ByteString, Future[IOResult]] = 
 FileIO.fromPath (
   Paths.get("src/main/resources/logfile.txt")
   chunkSize = 1024
 )
```
```FileIO.fromPath```

* Creates a ```source``` of ```ByteString``` from a file using a thread pool backed dispatcher dedicated for FileIO 
* Pulls data from the file and pushes it downstream whenever there is demand. 
* Completes when the end of the file is reached. 
* Use the ```Framing``` API and a decoder to parse ByteStrings into lines of text. 