package com.lightbend.faq.streams.graphs

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{GraphDSL, Keep, RunnableGraph, Sink, Source, Zip, ZipWith}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by hungai on 31/08/2017.
  */
object PartialGraphs extends App {

  implicit val system = ActorSystem("partial")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val pairs = Source.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val zip = b.add(Zip[Int, Int]())
    def ints = Source.fromIterator(() => Iterator.from(1))

    ints.filter(_ % 2 != 0) ~> zip.in0
    ints.filter(_ % 2 == 0) ~> zip.in1

    SourceShape(zip.out)
  })

  val firstPair: Future[(Int, Int)] = pairs.runWith(Sink.head)

  firstPair.onComplete(_ => system.terminate())


  case class PriorityWorkerPoolShape[In,Out] () extends Shape

}
