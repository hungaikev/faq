package com.lightbend.faq.streams.sources

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by hungai on 13/10/2017.
  */
object SourcesFromIterators extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  /**
    * Source.fromIterator -> Creates a source from an Iterator
    *    -> The Iterator is created each time the source is materialized
    *    -> Pushes elements from the Iterator whenever there is demand
    *    -> Completes when `hasNext`returns false
    */
  val sourceFromIterator: Source[Int, NotUsed] = Source.fromIterator(

    () => Iterator.from(0)
  )

  /**
    * Source.cycle -> Similar to Source.fromIterator, but the Iterator is infinitely repeated
    *    -> When `hasNext` returns false, the Iterator is recreated and consumed again
    */
  val sourceFromCycle: Source[Int, NotUsed] = Source.cycle(
    () => Iterator.range(1, 10)
  )


  sourceFromIterator.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())


}
