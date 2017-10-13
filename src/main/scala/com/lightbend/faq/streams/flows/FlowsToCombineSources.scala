package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object FlowsToCombineSources extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val source: Source[String, NotUsed] = Source.single("Hungai Kevin Amuhinda Anzweni Esibwe")

  /**
    * Flow.mapConcat -> Transform each element into zero or more elements that are individually passed downstream.
    *                ->
    */
  val words: Flow[String,String,NotUsed] = Flow[String].mapConcat(str => str.split(" ").toVector)


  /**
    * Flow.zip -> Combines the incoming elements with elements from another Source.
    *          -> Combines elements from each of multiple sources into tuples and passes the tuples downstream.
    *          -> Result is emitted as a tuple of both values
    */

  val zipWithIndex: Flow[String,(String,Int), NotUsed] = Flow[String].zip {
     Source.fromIterator(() => Iterator.from(0))
  }

  val sink: Sink[(String,Int),Future[Done]] = Sink.foreach(println)

  source.via(words).via(zipWithIndex).runWith(sink).onComplete(_ => system.terminate())

}
