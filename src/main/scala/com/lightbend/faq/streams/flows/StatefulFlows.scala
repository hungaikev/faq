package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object StatefulFlows extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  val source : Source[Int, NotUsed] = Source.unfold(0) {
    case value if value <= 100 => Some((value+1, value))
    case _ => None
  }

  val sink: Sink[Int, Future[Done]] = Sink.foreach(println)


  /**
    * Flow.fold -> Allows a stateful transformation by passing previous state into the next iteration
    */
  val computeSum: Flow[Int, Int, NotUsed] = Flow[Int].fold(0) {
    case (sum, value) => sum + value
  }

  /**
    * Flow.scan -> Allows for stateful transformation by passing previous state into the next iteration
    *           -> Unlike fold, scan will emit each new computed result
    *
    */
  val accumulate: Flow[Int, Int, NotUsed] = Flow[Int].scan(0) {
    case (acc, value) => acc + value
  }

  source.via(accumulate).via(computeSum).runWith(sink)
    .onComplete(_ => system.terminate())

}
