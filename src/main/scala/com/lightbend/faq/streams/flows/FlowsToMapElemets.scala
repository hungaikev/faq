package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object FlowsToMapElemets extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher


  val countTo100: Source[Int,NotUsed] = Source.unfold(0){
    case value if value <= 1000 => Some((value+1, value))
    case _ => None
  }

  /**
    * Flow.map -> Transforms the stream by applying the given function to each element
    *
    */
  val doubler : Flow[Int, Int, NotUsed] = Flow[Int].map( _ * 2)

  /**
    * Flow.mapAsync -> Accepts a function that returns a future but still guarantees ordering
    *               -> Parallelism defines the amount of parallelism to use when resolving the futures
    *
    * Flow.mapAsyncUnordered -> Accepts a function that returns a future and does not guarantee ordering
    */
  val asyncDoubler: Flow[Int, Int, NotUsed] = Flow[Int].mapAsync(parallelism = 4) {  i =>
    Future{ i * 2 }
  }


  val newDoubleSource = countTo100.via(doubler)

  val newAsyncDoubleSource = countTo100.via(asyncDoubler)

  val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](i => println(s"Value : $i"))

  //newDoubleSource.runWith(sink)

  newAsyncDoubleSource.runWith(sink).onComplete(_ => system.terminate())

}
