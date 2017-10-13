package com.lightbend.faq.streams.sources

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by hungai on 13/10/2017.
  */
object SourcesFromActors  extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  case class Message(value: String)

  /**
    * Source.actorRef  -> Creates a Source that is materialized as an ActorRef.
    *                  -> Messages sent to the ActorRef will be pushed to the stream or buffered until there is demand
    *                  -> Completes by sending the actor `akka.actor.Status.Success` or `akka.actor.PoisonPill`
    *                  -> `bufferSize` Determines the capacity of the buffer
    *                  -> `overflowStrategy` Determines what to do if the buffer overflows
    */


  val source: Source[Message, ActorRef] = Source.actorRef[Message](
    bufferSize = 10,
    overflowStrategy = OverflowStrategy.dropNew
  )

  source.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())


}
