package com.lightbend.faq.streams.sinks

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object SinksThatIgnoreElements  extends App {


  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher


  val countTo100: Source[Int, NotUsed] = Source.unfold(0) {
    case value if value <= 100 => Some((value + 1, value))
    case _ => None
  }


  /**
    * Sink.ignore -> Pulls all elements in the stream and discards them without processing
    */
  val ignoreSink : Sink[Any, Future[Done]] = Sink.ignore

  val graph = countTo100.runWith(ignoreSink)
    .onComplete(_ => system.terminate())

}
