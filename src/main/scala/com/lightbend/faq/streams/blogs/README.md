

All of the code in the post assumes the akka-stream artifact of at least version 2.5.4 to be present, 
and the following code implicitly being present in all samples. 


#### Using Akka Streams for small/moderate scale ETL or simple processing pipeline
 
 A small/moderate scale ETL or simple processing pipeline.Akka Streams is among the best tools for data cleaning--  the graph dsl is so easy to code with and reason about.
 
 The details: Batch processing to clean and curate data, with external calls RESTful requests as part of the flow. 
 
 Source[A] (read from file or DB) ~> Flow[A,B] (some transformation function) ~>  Flow[B,C] (by way of a RESTful request/response) ~> Flow[C,D] (graph query) ~> Sink[D] (to DB)
 
 Where Source might be 50,000 lines in a file or rows in a table. 
 
 
 1) Read multiple files
 2) Transform and merge data into one record 
 3) Make web service call (to machine learning API)
 4) Write files or database inserts
 
#### Moving Data from Cassandra to s3 in a compressed format and then back to Cassandra.


#### Testing streaming-data systems

I often find myself writing tests to validate the outputs of streams, be they data transformations, writes to a database,
or updates to intermediate streaming-data calculations. These tests focus on the business logic and the correctness of the output. 

But what if no elements are passed through the stream within the expected time frame, perhaps the assertions in the test are never even applied? Idle timeouts are great for codifying and asserting expectations regarding messaging latency, 
testing for this in addition to the correctness of the output, when designing complex functional tests for a distributed system



After getting started with actors and welcoming the benefits of this approach, 
it is not uncommon for people to encounter traditional concurrent-programming and distributed-systems 
problems—related to flow control, out-of-memory exceptions, or poor performance—which can be somewhat discouraging. 
This is usually when people discover the Akka Streams API

### How to wrap a stream inside an actor and have the actor restart the entire stream on failure. 
 

###  Retry logic in Akka streams. 

