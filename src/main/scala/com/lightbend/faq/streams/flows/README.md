## Flows

                                                                                             
```Flow``` - Basically an ordered collection of transformations that act upon the data from the ```Source``` consists of exactly one 
output and one input. It takes 3 type parameters 

1. For the input data type 
2. For the output
3. For the auxiliary type

```scala
 val flowCascade: Flow[Int, Boolean, NotUsed] = Flow[Int].filter((num => num > 2)).map((num) => num % 2 == 0)
```
