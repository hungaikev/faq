package com.lightbend.faq.streams.articles

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by hungai on 27/09/2017.
  */
object Examples extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  def myStage(name: String): Flow[Int,Int,NotUsed] =
    Flow[Int].map { index =>
      println(s"Stage $name is processing $index using ${Thread.currentThread().getName}")
      index
    }

  val normalSource = Source(1 to 1000)
    .via(myStage("A"))
    .via(myStage("B"))
    .via(myStage("C"))
    .via(myStage("D"))
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())


  val concurrentSource = Source(1 to 100000)
    .via(myStage("A")).async
    .via(myStage("B")).async
    .via(myStage("C")).async
    .via(myStage("D")).async
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())


  /*
  def writeToKafka(batch: Seq[Int]): Future[Unit] =
    Future {
      println(s"Writing  ${batch.size} elements to Kafka using this thread ->  ${Thread.currentThread().getName}")
    }


  val mapAsyncStage = Source(1 to 1000000)
    .grouped(100)
    .mapAsync(10)(writeToKafka)
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())

  val groupedWithinExample =
    Source(1 to 100000)
      .groupedWithin(100, 100.millis)
      .map(elements  => s"Processing ${elements.size} elements")
      .runForeach(println)
      .onComplete(_ => system.terminate())

  val groupedExample =
    Source(1 to 1000000)
      .grouped(100)
      .runForeach(println)
      .onComplete(_ => system.terminate())
*/



}
