## Sinks

```Sink``` - The receiver of the date after its transformed by the ```Flow``` consists of exactly one input. It is the subscriber of the data
sent/processed by a ```Source```. Usually it outputs its input to some system IO( TCP port, console, file, etc) Creating the side effect
of our application working. It is basically a ```Flow``` which uses a ```foreach``` or ```fold``` function to run a procedure(function with 
no return value, Unit) over its input elements and propagate the auxiliary value(eg a ```Future``` that will complete when it finishes writing the input
to a file or console)

Could be writing data to a file, database, REST API, collection etc. 

Sinks create back pressure by controlling demand. 

Sinks are define as ```Sink[-In, +Mat]```

* ```In``` defines the type of the elements the sink consumes.
* ```Mat``` defines the type of materialized value


Sinks send demand upstream. 

A sink should only send demand when it is ready to receive more data. 

If a sink can not keep up with incoming data, demand will stop and the data flow will cease. 

### **Sinks that ignore elements**

```scala

val ignore: Sink[Any, Future[Done]] = Sink.ignore 

```
```Sink.ignore```
* Pulls all elements in the stream and discards them without processing. 

### **Sinks with no materialized values**

```scala
val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](i => println(s"Value: $i"))

```
```Sink.foreach```
* Pulls elements from the stream and executes a block of code on each. 
* Executed purely for side effects. 
* No value is returned.

### **Sinks that materialize a single element**

```scala

val head: Sink[Int, Future[Int]] = Sink.head[Int]
val last: Sink[Int, Future[Int]] = Sink.last[Int]
val headOption: Sink[Int, Future[Option[Int]]] = Sink.headOption[Int]
val lastOption: Sink[Int, Future[Option[Int]]] = Sink.lastOption[Int]

```

```Sink.head, Sink.last```
* Pulls until it finds the first or last element in the stream and materializes it. 
* Fails with ```NoSuchElementException``` if the stream is empty

```Sink.headOption, Sink.lastOption```
* Similar to head/last except it returns ```None``` if the stream is empty. 


### **Sinks that materialize all elements**

```scala
val toSeq: Sink[Int, Future[Seq[Int]]] = Sink.seq[Int]

```

```Sink.seq```
* Pulls all elements in the stream and populates a sequence. 
* Materializes the sequence through a Future that completes when the stream completes. 

### **Stateful Sinks with a materialized value**

```scala
val foldSum: Sink[Int, Future[Int]] = Sink.fold[Int,Int](0) {
 case (sum, elem) => sum + elem 
}

val reduceSum: Sink[Int, Future[Int]] = Sink.reduce[Int] {
 case (sum, elem) => sum + elem
}
```
```Sink.fold```

* Pulls the elements of the stream and ```Fold``` them into a single element.
* Requires an initial value for the first iteration. 
* The result of the fold is encapsulated in the materialized value. 

```Sink.reduce```

* Similar to fold 
* The first element is used as the initial value. 

### **Sinks from actors**

```scala
case object PrintSum 

val sumActor = system.actorOf(Props(
  new Actor with ActorLogging {
    private var num = 0 
    case value: Int => sum += value 
    case PrintSum => log.info(sum.toString)
  }
))

val computeSum: Sink[Int, NotUsed] = Sink.actorRef[Int](sumActor, onCompleteMessage = PrintSum)

```

```Sink.actorRef```

* Pulls all elements in the stream and sends them to a provided actorRef
* When the stream completes it will send the onCompleteMessage to the actor. 
* No back pressure mechanism is provided. Beware of mailbox overflow. 
* ActorRefWithAck can provide back pressure. 


### **Sinks to files**

```scala
val writeToFile: Sink[ByteString, Future[IOResult]] = FileIO.toPath("myfile.txt")

```
```FileIO.toPath```

* Pulls ByteStrings from upstream ans writes them to a file. 

