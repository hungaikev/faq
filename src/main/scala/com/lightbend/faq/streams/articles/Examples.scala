package com.lightbend.faq.streams.articles

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision, ThrottleMode}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}
/**
  * Created by hungai on 27/09/2017.
  */
object Examples extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val source = Source(0 to 10)
    .map(n => n * 2)
    .watchTermination() { (_, done) =>
      done.onComplete {
        case Success(_) => println("Stream completed successfully")
          system.terminate()
        case Failure(error) => println(s"Stream failed with error ${error.getMessage}")
          system.terminate()
      }
    }
    .runWith(Sink.foreach(println))



  /*

val decider: Supervision.Decider = {
  case _: ArithmeticException =>
    println("Dropping element because of the Arithmetic Exception (Division by zero)")
    Supervision.Resume
  case _ => Supervision.Stop
}


implicit val materializer = ActorMaterializer(
  ActorMaterializerSettings(system).withSupervisionStrategy(decider)
)


val source = Source(0 to 5).map(100 / _)
val result = source.runWith(Sink.foreach(println)).onComplete(_ => system.terminate())





  def myStage(name: String): Flow[Int,Int,NotUsed] =
    Flow[Int].map { index =>
      println(s"Stage $name is processing $index using ${Thread.currentThread().getName}")
      index
    }

  //Run one Runnable graph at a time to see the difference. Observe the threads in both.

  val concurrentGraph = Source(1 to 10)
    .via(myStage("A")).async
    .via(myStage("B")).async
    .via(myStage("C")).async
    .via(myStage("D"))
    .runWith(Sink.ignore)
    .onComplete(_ => system.terminate())





def writeToDB(batch: Seq[Int]): Future[Unit] =
Future {
  println(s"Writing  ${batch.size} elements to the Database using this thread ->  ${Thread.currentThread().getName}")
}


val throttlerGraph2 = Source(1 to 10000)
  .grouped(10)
  .throttle(elements = 10, per = 1 second, maximumBurst = 10, ThrottleMode.shaping)
  .mapAsync(10)(writeToDB)
  .runWith(Sink.ignore)
  .onComplete(_ => system.terminate())




  val throttleGraph = Source(1 to 1000000)
   .map(n => s"I am number $n")
   .throttle(elements = 1, per = 1 second, maximumBurst = 1, mode = ThrottleMode.shaping)
   .runWith(Sink.foreach(println))
   .onComplete(_ => system.terminate())


val rateLimitedGraph = Source(1 to 100000)
.groupedWithin(100, 100.millis)
.mapAsync(10)(writeToDB)
.runWith(Sink.ignore)
.onComplete(_ => system.terminate())



val rateLimitedGraphUnordered = Source(1 to 100000)
.groupedWithin(100, 100.millis)
.mapAsyncUnordered(10)(writeToDB)
.runWith(Sink.ignore)
.onComplete(_ => system.terminate())


def myStage(name: String): Flow[Int,Int,NotUsed] =
Flow[Int].map { index =>
  println(s"Stage $name is processing $index using ${Thread.currentThread().getName}")
  index
}



val normalGraph = Source(1 to 1000)
.via(myStage("A"))
.via(myStage("B"))
.via(myStage("C"))
.via(myStage("D"))
.runWith(Sink.ignore)
.onComplete(_ => system.terminate())


val concurrentGraph = Source(1 to 100000)
.via(myStage("A")).async
.via(myStage("B")).async
.via(myStage("C")).async
.via(myStage("D")).async
.runWith(Sink.ignore)
.onComplete(_ => system.terminate())



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
