### Fan In Junctions 

1. ```Merge[In]``` - (N inputs, 1 output) randomly selects from inputs and pushes to a single output

2. ```MergePreferred[In]``` - (N inputs, 1 output) similar to Merge, but one output is given higher priority over all others.

3. ```ZipWith[A,B, ....,Out]``` - (N inputs, 1 output) similar to Merge, but one output is given higher priority over all others. 

4. ```Zip[A,B]``` - (2 inputs, 1 output) Zips two streams of A and B into a single stream of ```Tuple2[A,B]```

5. ```Concat[A]``` - (2 inputs, 1 output) concatenates 2 streams. Consumes one completely before the other. 


### Fan Out Junctions 

1. ```Broadcast[T]``` - (1 input, N outputs) Incoming elements are emitted to all outputs. 

2. ```Balance[T]``` - (1 input, N outputs) Incoming elements are emitted to one of the outputs (first available)

3. ```UnzipWith[In, A, B, ....]``` - (1 input, N outputs) Uses a function to convert 1 input element into N output elements 
and emits one to each output

4. ```Unzip[A,B]``` - (1 input, 2 outputs) Splits a stream of ```Tuple2[A,B]``` into two streams of A and B 