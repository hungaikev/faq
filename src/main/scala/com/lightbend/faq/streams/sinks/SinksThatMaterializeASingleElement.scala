package com.lightbend.faq.streams.sinks

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object SinksThatMaterializeASingleElement extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher


  val countTo100: Source[Int,NotUsed] = Source.unfold(0){
    case value if value <= 100 => Some((value+1, value))
    case _ => None
  }


  /**
    * Sink.head, Sink.last  -> Pulls until it finds the first or last element in the stream and materializes it.
    *
    * Fails with NoSuchElementException if the stream is empty.
    */

  val head: Sink[Int, Future[Int]] = Sink.head[Int]

  val last: Sink[Int, Future[Int]] = Sink.last[Int]


  /**
    * Sink.headOption,Sink.lastOption -> Similar to head/lst except it returns None if the stream is empty.
    */

  val headOption: Sink[Int, Future[Option[Int]]] = Sink.headOption[Int]

  val lastOption: Sink[Int, Future[Option[Int]]] = Sink.lastOption[Int]



  countTo100.runWith(headOption)
    .onComplete(_ => system.terminate())


}
