package com.lightbend.faq.streams.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by hungai on 26/08/2017.
  */


object BasicMaterializer extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val intSource: Source[Int, NotUsed] = Source(1 to 100)
  val headSink: Sink[Int, Future[Int]] = Sink.head[Int]

  val graph1: RunnableGraph[Future[Int]] = intSource.toMat(headSink)(Keep.right)
  val graph2: RunnableGraph[NotUsed]     = intSource.toMat(headSink)(Keep.left)

  // we can only get values from graph1
  val result = Await.result(graph1.run(), Duration(3,"seconds"))

  println(result)

  system.terminate()
}