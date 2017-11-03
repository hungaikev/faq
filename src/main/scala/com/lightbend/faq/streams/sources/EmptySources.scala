package com.lightbend.faq.streams.sources

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by hungai on 13/10/2017.
  */
object EmptySources extends  App {

  /**
    * Empty Sources
    */

  implicit val system = ActorSystem("jump")
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  /**
    * Source.empty -> Creates an empty Source of the specified type
    *       -> Always completes the stream
    *
    * Useful to test how your system responds when there is no data.
    *
    */

  val source: Source[String,NotUsed] = Source.empty[String]

  source.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())

}
