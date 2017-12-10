## FAQ  IDEAS


### How to implement batching logic in Akka Streams 

A common request we see with streaming data is the need to take the stream of elements and group them together (i.e. committing data to a database, a message queue or disk). Batching is usually a more efficient and performant solution than writing a single piece of data at a time.

Using the Akka Streams API, grouping messages is as easy as adding a `grouped` element. 

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

However, grouping often introduces an unacceptable latency. To address this, you can use the `groupedWithin` method to group elements within a bounded time frame. This operation takes two parameters, a maximum batch size **and** a batch cutoff time, which are used to batch together either the specified number of elements or as many elements as are received during the specified duration. Even if the maximum number of elements has not been satisfied, once the specified duration is reached the current grouping will be emitted.  

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

For `grouped` - See more in the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#grouped ) or [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#grouped ). 

For `groupedWithin` - See more in the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#grouped ) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#groupedwithin). 

###  How to do Rate Limiting in Akka Streams. 

In certain scenarios it is important to limit the number of concurrent requests to other services. For example, to avoid overwhelming the services and avoid performance degradation, or to maintain service level agreements. This is particularly important when streams are unbounded and the message rates are dynamic. 

No matter the scenario, the Akka Streams API provides a seamless way to do this through back pressure applied upstream. 

The following example shows how to batch elements, then asynchronously write the batched elements to a database, limiting the number of outstanding requests to only 10. 

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  def writeToDB(batch: Seq[Int])= Future {
    println(s"Writing ${batch.size} elements to the DB using thread '${Thread.currentThread().getName}'")
  }


  val rateLimitedGraph = Source(1 to 100000)
    .groupedWithin(100, 100.millis)
    .mapAsync(10)(writeToDB)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())

```

The above example preserves the order of the elements downstream, which can be important depending on the application. 
If downstream order of elements is not important the Akka Streams API provides `mapAsyncUnordered`.

For more on `mapAsync` see the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasync ) or [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasync ). 

For more on `mapAsyncUnordered` see the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasyncunordered ) or [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasyncunordered).

###  How to do throttling in Akka Streams 

When building a streaming application and the upstream exceeds the specified rate the `throttle` element can 
fail the stream or shape the stream by back pressuring. Throttling with Akka Streams API is as easy as adding a `throttle` element and add specific number of elements per time unit. 

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val throttleGraph = Source(1 to 1000000)
     .map(n => s"I am number $n")
     .throttle(elements = 1, per = 1 second, maximumBurst = 1, mode = ThrottleMode.shaping)
     .runWith(Sink.foreach(println))
     .onComplete(_ => system.terminate())
```

Once the upper bound has been reached the parameter `maximumBurst` can be used to allow the client to send a 
burst of messages while still respecting the `throttle`

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def writeToDB(batch: Seq[Int]) = Future {
    println(s"Writing ${batch.size} elements to the DB using thread '${Thread.currentThread().getName}'")
  }

  val throttlerGraph2 = Source(1 to 10000)
    .grouped(10)
    .throttle(elements = 10, per = 1 second, maximumBurst = 10, ThrottleMode.shaping)
    .mapAsync(10)(writeToDB)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())
```

For more on `throttle` see the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#throttle) or [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#throttle).

###  How to execute streams asynchronously

In certain situations where there is need an asynchronous operation with back pressure handled. We use
`mapAsync`  or `mapAsyncUnordered` depending on whether ordering for the elements is required or not. 
`mapAsync` takes a parallelism parameter and a function returning a `Future`. The `parallelism` parameter 
allows us to specify how many simultaneous operations are allowed. 

Performing asynchronous computations with Akka Streams API is as easy as adding the `mapAsync` or `mapAsyncUnordered` to a stage on the stream. 

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def writeToKafka(batch: Seq[Int])= Future {
      println(s"Writing ${batch.size} elements to Kafka using thread '${Thread.currentThread().getName}'")
  }

  val mapAsyncStage = Source(1 to 1000000)
    .grouped(100)
    .mapAsync(10)(writeToKafka)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())
```


For more on `mapAsync` see the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasync ) or [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasync ). 

For more on `mapAsyncUnordered` see the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#mapasyncunordered ) or [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#mapasyncunordered).



###  Concurrency in Akka Streams.

To construct efficient, scalable and low -latency streaming data systems, it is very important to perform tasks concurrently. 

If you want elements in the stream to be processed in parallel, you must request Akka Streams directly because by default Akka Streams
executes sequentially on a single thread. 

To allow for parallel processing you will have to insert asynchronous boundaries manually into your flows and graphs by way of 
adding `Attributes.asyncBoundary` using the method `async` on `Source`, `Sink` and `Flow` to pieces that shall 
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

```

Observe the threads in both. 

```scala


  val concurrentGraph = Source(1 to 100000)
    .via(myStage("A")).async
    .via(myStage("B")).async
    .via(myStage("C")).async
    .via(myStage("D")).async
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())
 

```

See more in the [Java](http://doc.akka.io/docs/akka/current/java/stream/stream-flows-and-basics.html#operator-fusion) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stream-flows-and-basics.html#operator-fusion).


###  How to do Error handling and recovery. 

When developing applications we should not assume that there will be no unexpected issues. Akka provides a set of 
supervision strategies to deal with errors that happens in actors. Akka streams is no different  
and its error handling strategies were inspired by actor supervision strategies.
 
There are three ways to handle exceptions in your application code: 
 * `Stop`  - The stream is completed with failure
 * `Resume` - The element is dropped and the stream continues
 * `Restart` - The element is dropped and the stream continues after restarting the stage. Restarting the stage means that any accumulated state is cleared. 

Default supervision strategy for a stream can be defined on the settings of the materializer for the whole stream or for a particular stage. 

```scala

 implicit val system = ActorSystem()

  implicit val ec = system.dispatcher

  val decider: Supervision.Decider = {
    case _: ArithmeticException =>
      println("Dropping element because of the Arithmetic Exception (Division by zero)")
      Supervision.Resume
    case _ => Supervision.Stop
  }


  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(decider)
  )


  val source = Source(0 to 5).map(100 / _)
  val result = source.runWith(Sink.foreach(println)).onComplete(_ => system.terminate())

```

Akka Streams also provides a `RestarSource`, `RestartSink`, `RestartFlow` for implementing the so called exponential 
backoff supervision strategy starting a stage again when it fails each time with a growing time delay between restarts. 


See more in the [Java](http://doc.akka.io/docs/akka/current/java/stream/stream-error.html) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stream-error.html).



### Terminating a stream.

Streams do not run on the caller thread, instead they run on a different thread in the background, without blocking the caller.
Therefore we need to terminate the underlying actor system when the stream completes for the program to end. 
You can use the `Future` returned by `runWith` to terminate the actor system.

```scala
 
   implicit val system = ActorSystem()
   implicit val materializer = ActorMaterializer()
 
   implicit val ec = system.dispatcher
 
   val source = Source(0 to 10)
     .map(n => n * 2)
     .runWith(Sink.foreach(println)) // returns a Future[Done]
     .onComplete(_ => system.terminate())  // onComplete callback of the future

 
```

Akka Stream API also has a `watchForTermination` method that can be used to monitor stream termination both for success and failure cases.
This is usually a good place to add logging messages or trigger some follow-up actions.

```scala

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val source = Source(0 to 10)
    .map(n => n * 2)
    .watchTermination() { (_, done) =>
      done.onComplete {
        case Success(_) => println("Stream completed successfully")
          system.terminate()
        case Failure(error) => println(s"Stream failed with error ${error.getMessage}")
          system.terminate()
      }
    }
    .runWith(Sink.foreach(println))
```

See more in the [Java](http://doc.akka.io/docs/akka/current/java/stream/stages-overview.html#watchtermination) or the [Scala documentation](http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#watchtermination).



