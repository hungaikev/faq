 ### Shapes 
 Is a "box" with inputs and outputs, something that "processes" messages
 
 1. ```Shape```: The top abstract class for any shape. Contains an empty list of inputs and outputs
 2. ```SourceShape```: 0 -> 1 (a Source has a ```SourceShape``` and its the start of a Graph)
 3. ```SinkShape```: 1 -> 0 output only (a Sink has a ```SinkShape``` and its the end of a Graph)
 4. ```FlowShape```: 1 -> 1 
 5. ```BidiShape```: 2 -> 2 (1 -> 1 with 1 <- 1, bidirectional)
 6. ```FanOutShape```: 1 --> N (typically a Broadcast)
 7. ```FanInShape```: N --> 1 (typically a Merge)
 8. ```ClosedShape```: Shape with closed inputs and closed outputs that can be materialized

```scala


Source.fromFuture(Future { 2 })
    .via(Flow[Int].map(_ * 2))
    .to(Sink.foreach(println))
    
```
    
The code above is the same as the code below

```
val g: RunnableGraph[_] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder => 
   val source = builder.add(Source.fromFuture(Future{ 2 }))
   val multiply = builder.add(Flow[Int].map(- * 2))
   val sink = builder.add(Sink.foreach(println))
   
   import GraphDSL.Implicits._ 
   
   source ~> multiply ~> sink 
   ClosedShape
})

```
### Fan In Junctions 

1. ```Merge[In]``` - (N inputs, 1 output) randomly selects from inputs and pushes to a single output. 
```Merge``` picks a signal randomly from one of many inputs and emits the event downstream. Variants are available such as 
```MergePreferred```, which favours a particular inlet if events are available to consume on multiple incoming streams, and ```MergeSorted```, 
which assumes the incoming streams are sorted and therefore ensures the outbound stream is also sorted.

2. ```MergePreferred[In]``` - (N inputs, 1 output) similar to Merge, but one output is given higher priority over all others.

3. ```ZipWith[A,B, ....,Out]``` - (N inputs, 1 output) similar to Merge, but one output is given higher priority over all others. 

4. ```Zip[A,B]``` - (2 inputs, 1 output) Zips two streams of A and B into a single stream of ```Tuple2[A,B]```

5. ```Concat[A]``` - (2 inputs, 1 output) concatenates 2 streams. Consumes one completely before the other. 


### Fan Out Junctions 

1. ```Broadcast[T]``` - (1 input, N outputs) Incoming elements are emitted to all outputs. ```Broadcast``` ingests 
events from one input and emits duplicated events across more than one output stream. An example usage for a broadcast 
would be to create a side-channel, with events from one stream being persisted to a data store, 
while the duplicated events are sent to another graph for further computation.

2. ```Balance[T]``` - (1 input, N outputs) Incoming elements are emitted to one of the outputs (first available). 
```Balance``` signals one of its output ports for any given signal, but not both. 
According to the documentation, “each upstream element is emitted to the first available downstream consumer”. 
This means events are not distributed in a deterministic fashion, with one output being signalled and then the other, 
but rather whichever downstream subscriber happens to be available. This is a very useful fan-out function for high-throughput streams, 
enabling graphs to be split apart and multiple instances of downstream subscribers replicated to handle the volume.

3. ```UnzipWith[In, A, B, ....]``` - (1 input, N outputs) Uses a function to convert 1 input element into N output elements 
and emits one to each output

4. ```Unzip[A,B]``` - (1 input, 2 outputs) Splits a stream of ```Tuple2[A,B]``` into two streams of A and B 


### Closed Graphs

ClosedShape indicates no open inputs or outputs. 

```scala
 RunnableGraph.fromGraph(GraphDSL.create() {
  implicit builder: GraphDSL.Builder[NotUsed] => 
  import GraphDSL.Implicts._ 
  val source = Source(1 to 10)
  val sink = Sink.foreach(println)
  val bcast = builder.add(Broadcast[Int](2))
  val merge = builder.add(Merge[Int](2))
  
  val f1, f2, f3, f4 = Flow[Int].map(_ + 10)
  
  source ~> f1 ~> bcast ~> merge ~> f4 ~> sink
                  bcast ~> merge
    ClosedShape
 }).run()
```

### Partial Graphs 

PartialGraphs can be in the shape of linear Graph elements. 

```scala
 val source: Source[Int, NotUsed] = Source.fromGraph(
  GraphDSL.create() {
   implicit builder: GraphDSL.Builder[NotUsed] => 
    import GraphDSL.Implicits._ 
    val source1, source2 = Source(1 to 10)
    val merge = builder.add(Merge[Int](2))
    
    source1 ~> merge
    source2 ~> merge
    
    SourceShape(merge.out)
  }
 )
```

Partial Graphs can be created in the shape of Junctions(eg Fan In, Fan Out)

```scala
val dualPortFanIn = GraphDSL.create() {
 implicit builder: GraphDSL.Builder[NotUsed] => 
  val merge = builder.add(Merge[Int](2))
  UniformFanInShape[Int,Int] (
   merge.out, 
   merge.in(0),
   merge.in(1)
  )
}
```































