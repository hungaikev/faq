## Flows

                                                                                             
```Flow``` - Basically an ordered collection of transformations that act upon the data from the ```Source``` consists of exactly one 
output and one input. It takes 3 type parameters 

1. For the input data type 
2. For the output
3. For the auxiliary type

```scala
 val flowCascade: Flow[Int, Boolean, NotUsed] = Flow[Int].filter((num => num > 2)).map((num) => num % 2 == 0)
```

Used to move data from a Source into a Sink, manipulating that data in some way( transforming, filtering, etc)

Defined as ```Flow[-In, +Out, +Mat]```
1. ```In``` - Defines the type of elements that the flow consumes. 
2. ```Out``` - Defines the type of the elements that the flow produces
3. ```Mat``` - Defines the type of materialized value

A flow receives Demand from downstream and propagates it. 

Like a Source, If there is no downstream demand, the flow must stop. 

Flows can propagate back pressure upstream by reducing or stopping demand. Alternatively Flows can drop data, or buffer data etc


### Flows to map elements 

```scala
 val double: Flow[Int, Int, NotUsed] = Flow[Int].map(_ * 2)
 
 val double2: Flow[Int].mapAsync(parrallelism = 4) {i => 
  Future{i * 2 }
 }
```

```Flow.map```
* Transforms the stream by applying the given function to each element.

```Flow.mapAsync```
* Accepts a function that returns a future but still guarantees ordering. 
* Parallelism defines the amount of parallelism to use when resolving the futures. 

```Flow.mapAsyncUnordered```
* Accepts a function that returns a future and does not guarantee ordering. 

### Flows to flatten elements 
```scala
 val words: Flow[String, String, NotUsed] = Flow[String].mapConcat(str => str.split("\\s")).toVector
 
```

```Flow.mapConcat```
* Transforms data into a collection that is flattened into the stream. 
* Similar to flatMap on a collection

### Flows to group elements


### Stateful Flows
 

### Flows to map elements 


### Flows to filter elements 


### Flows to limit  elements by time
 
 
### Flows to combine sources


### Flows to flatten sources


### Flows to buffer elements 


### Flows to map elements 


### Flows for slow consumers/producers

```Flow.expand```
* Extrapolates additional values from the incoming elements to fill gaps when the consumer is faster than the producer. 

```Flow.batch```
* Groups elements into a batch to be consumed downstream if the producer is faster than the consumer. 

```Flow.conflate```
* Create a summary of multiple elements to be consumed downstream if the producer is faster than the consumer. 


### Flows to log elements

```Flow.log```
* Write the elements in the stream to a log while passing them to the next stage. 
* A provided name is included in each log statement. 
* Optionally, an extract function can be provided that can extract information from the element to be logged. 
* Requires an implicit logging adapter 
* By default, elements are logged on debug level, but can be configured using ```withAttributes```