### Sources 

```Source``` -  as the name suggests the source of the data, consists of exactly one output. 
```Source``` takes two type parameters. The first one represents the type of data it emits and the second one is the type of 
the auxiliary value it can produce when ran/materialized. If we do not produce any we use the ```NotUsed``` type provided by akka. 