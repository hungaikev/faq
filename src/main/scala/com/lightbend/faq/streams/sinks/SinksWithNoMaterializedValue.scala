package com.lightbend.faq.streams.sinks

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object SinksWithNoMaterializedValue extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher


  val countTo100: Source[Int,NotUsed] = Source.unfold(0){
    case value if value <= 100 => Some((value+1, value))
    case _ => None
  }


  /**
    * Sink.foreach -> Pulls elements from the stream and executes a block of code on each
    *              -> Executed primarily for side effects
    *              -> No value is returned
    */
  val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](i => println(s"Value : $i"))

  countTo100.runWith(sink)
    .onComplete(_ => system.terminate())


}
