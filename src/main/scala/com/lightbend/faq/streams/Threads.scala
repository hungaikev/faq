package com.lightbend.faq.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
/**
  * Created by hungai on 26/09/2017.
  */
object Threads extends App {

  implicit val system = ActorSystem("LifecycleApp")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  def processingStage(name: String): Flow[String,String,NotUsed] =
    Flow[String].map { s =>
      println(name + " "+ "started processing" + " " + s + " " + "on thread" + " " + Thread.currentThread().getName)
      Thread.sleep(100)
      println(name +  " " + "finished processing " + " " + s)
      s
    }


  val completion = Source(List("Hello", "Streams","World"))
    .via(processingStage("A")).async
    .via(processingStage("B")).async
    .via(processingStage("C")).async
    .runWith(Sink.foreach(s => println("Got output"  + s)))

  completion.onComplete(_ => system.terminate())

}
