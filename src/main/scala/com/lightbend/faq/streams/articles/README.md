## FAQ  IDEAS

### 1. Using a dedicated dispatcher to run a stream.

See more in the Akka Streams documentation on Dispatchers (Java/Scala) 

### 2. Batching  in Akka Streams. 

This is a common pattern we see with streaming data. Basically you have a stream of elements and you need to group them together. 
It usually proves to be handy when you need to perform an operation that is more efficient in batch ie 
when committing data to a database, a message queue or write to disk. It is common to write the data in batch rather 
than writing a single piece of data at a time in order to gain superior performance. 

Grouping messages with Akka Streams API is as easy as adding a ```grouped``` element. 

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val groupedExample =
    Source(1 to 100000)
      .grouped(100)
      .runForeach(println)
      .onComplete(_ => system.terminate())

```

Grouping often introduces an unacceptable latency. To address this Akka Streams API has ```groupedWithin``` to group elements 
but also emit elements within a bounded time frame, even if the maximum number of elements has not been satisfied. This operation takes a duration
and batches together the number of elements you specified or as many elements received during the specified duration. 

```scala

   implicit val system = ActorSystem()
   implicit val materializer = ActorMaterializer()
   implicit val ec = system.dispatcher
 
   val groupedWithinExample =
     Source(1 to 100000)
       .groupedWithin(100, 100.millis)
       .map(elements  => s"Processing ${elements.size} elements")
       .runForeach(println)
       .onComplete(_ => system.terminate())

```

For ```grouped``` - See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#grouped ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#grouped ). 

For ```groupedWithin``` - See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#grouped ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#groupedwithin). 

### 3. How to do Rate Limiting in Akka Streams. 



See more in the Java documentation or the Scala documentation.

### 4. How to do Throttling in Akka Streams. 

See more in the Java documentation or the Scala documentation.

### 5. How to do Error handling and recovery. 

Failures vs Error handling in Akka Streams (What is an error and what is a failure)

- Using RestartFlow in a Stream.

- I think a nice guidelines how to decide is to split your exceptions into Errors and Failures. 


See more in the Java documentation or the Scala documentation.

### 6. Retry logic in Akka streams. 

See more in the Java documentation or the Scala documentation.

### 7. Concurrency in Akka Streams.

See more in the Java documentation or the Scala documentation.

### 8. Flattening a stream.

See more in the Java documentation or the Scala documentation.

### 9. Terminating a stream.

See more in the Java documentation or the Scala documentation.

### 10. Logging in a stream.

See more in the Java documentation or the Scala documentation.

### 11. Asynchronous Computations.

In certain situations where we need  an asynchronous operation with back pressure handled. We use
```mapAsync```  or ```mapAsyncUnordered``` depending on whether ordering for the elements is required or not. 
```mapAsync``` takes a parallelism parameter and a function returning a ```Future```. The ```parallelism``` parameter 
allows us to specify how many simultaneous operations are allowed. 

Performing asynchronous computations with Akka Streams API is as easy as adding the ```mapAsync``` or ```mapAsyncUnordered``` to a stage on the stream. 


```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def writeToKafka(batch: Seq[Int]): Future[Unit] =
    Future {
      println(s"Writing  ${batch.size} elements to Kafka using this thread ->  ${Thread.currentThread().getName}")
    }


  val mapAsyncStage = Source(1 to 1000000)
    .grouped(100)
    .mapAsync(10)(writeToKafka)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())

```


For ```mapAsync``` - See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasync ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasync ). 

For ```mapAsyncUnordered``` - See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasyncunordered ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasyncunordered).

