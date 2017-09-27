## FAQ  IDEAS
*************************************************

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

In certain scenarios it is important to limit the number of concurrent requests to other services, to avoid overwhelming these services and degrade performance.
Sometimes you need to maintain service level agreements, particularly when streams are unbounded and the message rates are dynamic. 

In all this scenarios Akka Streams API provides a seamless way to do this with back pressure applied upstream. 

The following example shows how to batch elements, then write the batched elements to a database, asynchronously, 
limiting the number of outstanding requests to only 10. 

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  def writeToDB(batch: Seq[Int]): Future[Unit] =
    Future {
      println(s"Writing  ${batch.size} elements to the Database using this thread ->  ${Thread.currentThread().getName}")
    }


  val rateLimitedGraph = Source(1 to 100000)
    .groupedWithin(100, 100.millis)
    .mapAsync(10)(writeToDB)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())

```


The above example preserves the order of the elements downstream which can be important depending on the application. 
If downstream order of elements is not important Akka Streams API provides ````mapAsyncUnordered````

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  def writeToDB(batch: Seq[Int]): Future[Unit] =
    Future {
      println(s"Writing  ${batch.size} elements to the Database using this thread ->  ${Thread.currentThread().getName}")
    }


  val rateLimitedGraphUnordered = Source(1 to 100000)
    .groupedWithin(100, 100.millis)
    .mapAsyncUnordered(10)(writeToDB)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())

```



For ```mapAsync``` - See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasync ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasync ). 

For ```mapAsyncUnordered``` - See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasyncunordered ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasyncunordered).

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

To construct efficient, scalable and low -latency streaming data systems, it is very important to perform tasks concurrently. 

If you want elements in the stream to be processed in parallel, you must request Akka Streams directly because by default Akka Streams
executes sequentially on a single thread. 

To allow for parallel processing you will have to insert asynchronous boundaries manually into your flows and graphs by way of 
adding ```Attributes.asyncBoundary``` using the method ```async``` on ```Source```, ```Sink``` and ```Flow``` to pieces that shall 
communicate with the rest of the graph in an asynchronous fashion. 

Choosing which stage can be performed in parallel requires a good understanding of the different operations performed on the pipeline. 

```scala

 implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  def myStage(name: String): Flow[Int,Int,NotUsed] =
    Flow[Int].map { index =>
      println(s"Stage $name is processing $index using ${Thread.currentThread().getName}")
      index
    }

//Run one Runnable graph at a time to see the difference. Observe the threads in both.  

  val normalGraph = Source(1 to 100000)
    .via(myStage("A"))
    .via(myStage("B"))
    .via(myStage("C"))
    .via(myStage("D"))
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())


  val concurrentGraph = Source(1 to 100000)
    .via(myStage("A")).async
    .via(myStage("B")).async
    .via(myStage("C")).async
    .via(myStage("D")).async
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())
 
```


See more in the [Java documentation](http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html#operator-fusion) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stream-flows-and-basics.html#operator-fusion).

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

