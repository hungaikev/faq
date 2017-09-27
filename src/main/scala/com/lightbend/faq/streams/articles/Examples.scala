package com.lightbend.faq.streams.articles

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import scala.concurrent.duration._

/**
  * Created by hungai on 27/09/2017.
  */
object Examples extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val groupedWithinExample =
    Source(1 to 100000)
      .groupedWithin(100, 100.millis)
      .map(elements  => s"Processing ${elements.size} elements")
      .runForeach(println)
      .onComplete(_ => system.terminate())

  /*
  val groupedExample =
    Source(1 to 1000000)
      .grouped(100)
      .runForeach(println)
      .onComplete(_ => system.terminate())
*/



}
