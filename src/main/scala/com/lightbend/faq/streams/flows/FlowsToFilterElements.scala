package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object FlowsToFilterElements extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  /**
    * Source.unfold -> Stream the result of a function as long as it returns a Some ,
    *                  the value inside the option consists of a tuple where the first value is a state passed
    *                  back into the next call to the function allowing to pass a state.
    *                  The first invocation of the provided fold function will receive the zero state.
    */
  val source: Source[Int, NotUsed] = Source.unfold(0) {
    case value if value <= 100 => Some((value + 1, value))
    case _ => None
  }


  val sink: Sink[Int, Future[Done]] = Sink.foreach(println)

  /**
    * Flow.filter -> Filters elements in/out of the stream
    */

  val oddNumbers: Flow[Int, Int, NotUsed] = Flow[Int].filter(_ % 2 == 1)

  /**
    * Flow.collect -> Apply a partial function to elements in the stream
    *              -> Materialised elements are transformed by the function
    *              -> Unmatched elements are dropped
    */

  val evenNumbers: Flow[Int,Int,NotUsed] = Flow[Int].collect {
    case num if num % 2 == 0 => num
  }

  source.via(evenNumbers).runWith(sink).onComplete(_ => system.terminate())
}
