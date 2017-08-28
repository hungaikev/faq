## Runnable Graphs

### Accessing Materialized Values

```scala
 val source = Source.tick(1.second, 1.second, "Hello World")
 val sink = Sink.foreach[Any](println)
 val flow = Flow[String].map(_.split(" "))
 
 val matSource: Cancellable = source.to(sink).run()
 val matFlow: NotUsed = source.viaMat(flow)(Keep.right).to(sink).run()
 val matSink: Future[Done] = source.toMat(sink)(Keep.right).run()
 
```

1. ```to``` - materializes the value from the stage it s called on. 

2. ```toMat``` - Allows you to transform/combine the materialized value. ```toMat``` accepts a Sink and a function to combine materialized values. 
 Keep includes predefined functions for ```both, left, right```

3. ```viaMat``` - Similar to ```toMat``` but operates on a Flow instead of a Sink

4. ```run``` - Executes the ```RunnableGraph``` and returns the materialized value. 


### Attributes 

```scala

Source(1 to 100)
  .withAttributes(ActorAttributes.dispatcher("my-dispatcher"))
  .runForeach(println)

```

Attributes of a stage can be tuned using the ```withAttributes``` method. It allows you to customize aspects of the stage like: 

* Dispatcher 
* Buffer stages
* Log Levels
* Supervision. 

### Fault Tolerance 

```scala
 implicit val materializer = ActorMaterializer( 
  ActorMaterializerSettings(system)
     .withSupervisionStratefy(decider)
 )
```

Similar to Actors, exceptions in streams can be handled using a configurable supervision Strategy. 

The default strategy is to stop all processing of the stream. The default strategy can be overriden on the materializer. 


### Custom Supervision 


```scala
 val decider: Supervision.Decider = {
  case _: MyException => Supervision.Resume
  case _ => Supervision.Stop 
 }
```


Decider is a function from Throwable to Supervision.Directive

Available Directives are: 

1. ```Stop``` - Stream is terminated with an error. 
2. ```Resume``` - Failing element is dropped and the stream continues. 
3. ```Restart``` - The element is dropped and the stream continues after restarting the stage. 
Any state accumulated by the stage will be cleared. 

### Custom Supervision per Stage
```scala

val decider: Supervision.Decider = {
 case _: ArithmeticException => Supervision.resume 
 case _ => Supervision.Stop 
}

val possibleDivisionByZero = Flow[Int].map(i => 100/i)
  .withAttributes(
    ActorMaterializerSettings.supervisionStrategy(decider)
  )

```

Attributes can be sed to customize supervision per stage. 
Allows you to supply a Supervision strategy that will apply only to the stage. 


### Recover a Stream 

```scala
 val recoverWithZero: Flow[Int,Int,NotUsed] = 
   Flow[Int].recover {
     case _: ArithmeticException => 0 
   }
```

Sometimes when a failure occurs, It may be desirable to terminate the stream immediately with a specific value. 
The ```recover``` takes a ```PartialFunction[Throwable,T]``` It will terminate the stream gracefully parsing the 
resulting value as the final value in the stream 
















