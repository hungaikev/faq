package com.lightbend.faq.streams.sources

import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import scala.concurrent.duration._

/**
  * Created by hungai on 13/10/2017.
  */
object SingleElementSources  extends App {

  implicit val system = ActorSystem("jumpman")
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  /**
    * Source.single -> Creates a Source with  a single arbitrary element.
    *        -> Push a single element and the complete.
    */

  val singleSource: Source[String,NotUsed] = Source.single( "Hello World" )


  /**
    * Source.repeat -> Similar to Source.single but the single element is infinitely pushed whenever there is demand
    *
    */

  val repeatSource: Source[String,NotUsed] = Source.repeat("Hello world")


  /**
    * Source.tick -> Similar to Source.repeat except the element is pushed on a time schedule
    *             -> If there is no demand (i.e back pressure) no tick will be pushed. That tick will be lost
    */

  val tickSource: Source[String,Cancellable] = Source.tick(
    initialDelay = 1.second,
    interval = 5.seconds,
    tick = "Hello World"
  )

  tickSource.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())

}
