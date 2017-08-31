package com.lightbend.faq.streams

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}

/**
  * Created by hungai on 29/08/2017.
  */
object QuickStart  extends App {

  implicit val system = ActorSystem("QuickStart")

  implicit val mat = ActorMaterializer()

  implicit val ec = system.dispatcher

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  def lineSink(filename:String): Sink[String,Future[IOResult]] =
    Flow[String]
    .map(s => ByteString(s + "\n"))
    .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  val result: Future[IOResult] = factorials
    .map(_.toString)
    .runWith(lineSink("factorial2.txt"))

  result.onComplete(_ => system.terminate())

}
