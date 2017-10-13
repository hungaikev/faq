package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object FlowsToFlattenElements extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val countTo100 : Source[Int,NotUsed] = Source.unfold(0) {
    case value if value <= 100 => Some((value + 1, value))
    case _ => None
  }

  /**
    * Flow.mapConcat -> Transforms data into a collection that is "flattened" into the stream.
    *                -> Similar to a flatMap on a collection
    */


  val stringSource: Source[String,NotUsed] = Source.single("Hungai Kevin Amuhinda")

  val words: Flow[String,String,NotUsed] = Flow[String].mapConcat(str => str.split(" ").toVector)

  val sink: Sink[String, Future[Done]] = Sink.foreach(i => println(s"Here is your text $i"))

  stringSource.via(words).runWith(sink).onComplete(_ => system.terminate())

}
