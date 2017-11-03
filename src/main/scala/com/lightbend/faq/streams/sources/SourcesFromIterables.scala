package com.lightbend.faq.streams.sources

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by hungai on 13/10/2017.
  */
object SourcesFromIterables extends App {


  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  /**
    * Creates a Source from a collection.immutable.Iterable
    *  -> Elements are taken from the Iterable and pushed downstream whenever there is demand
    *  -> The Stream is completed if there is no more data in the Iterable
    */
  val source: Source[Int, NotUsed] = Source(1 to 10)

  source.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())


}
