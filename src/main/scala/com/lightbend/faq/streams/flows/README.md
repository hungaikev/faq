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

### Flows to flatten elements 

### Flows to group elements


### Stateful Flows
 

### Flows to map elements 


### Flows to filter elements 


### Flows to map elements 


### Flows to limit  elements by time
 
 
### Flows to combine sources


### Flows to flatten sources


### Flows to buffer elements 


### Flows to map elements 


### Flows for slow consumers/producers


### Flows to log elements