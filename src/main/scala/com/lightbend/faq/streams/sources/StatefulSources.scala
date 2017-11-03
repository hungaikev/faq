package com.lightbend.faq.streams.sources

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by hungai on 13/10/2017.
  */
object StatefulSources extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  /**
    * Source.unfold -> Uses an intial value and a transformation function
    *          -> The transformation function returns an Option of a Tuple containing the value of the next
    *          iteration and the value to push
    *          -> Completes when the transfomation function returns None
    *
    * Source.unfoldAsync -> Similar to unfold, but the function returns a Future of an Option.
    */


  //Emits value for the next stage and uses value+1 back on the stage
  val countTo100: Source[Int,NotUsed]= Source.unfold(0) {
    case value if value <= 100 => Some((value + 1, value))
    case _ => None
  }

  countTo100.runWith(Sink.foreach(println))
    .onComplete( _ => system.terminate())

}
