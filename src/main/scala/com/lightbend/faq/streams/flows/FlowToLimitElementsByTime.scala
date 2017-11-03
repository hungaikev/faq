package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration._
/**
  * Created by hungai on 13/10/2017.
  */
object FlowToLimitElementsByTime extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  val source: Source[Int, NotUsed] = Source.unfold(0) {
    case value if value <= 100 => Some((value+1, value))
    case _ => None
  }

  val sink: Sink[Int, Future[Done]] = Sink.foreach(println)

  val groupedSink: Sink[Seq[Int], Future[Done]] = Sink.foreach(println)

  /**
    * Flow.takeWithin -> Take elements from the stream for the given duration then terminate
    */

  val oneSecondOfData: Flow[Int,Int,NotUsed] = Flow[Int].takeWithin(1.second)


  /**
    * Flow.dropWithin -> Drop data for the specified period of time, then proceed with the rest
    */

  val skipOneSecondOfData: Flow[Int,Int,NotUsed] = Flow[Int].dropWithin(10.milliseconds)


  /**
    * Flow.groupedWithin -> Group Elements by the given number or time period whichever comes first
    */

  val groupBySecond: Flow[Int,Seq[Int],NotUsed] = Flow[Int].groupedWithin(10, 1.second)

  source.via(groupBySecond).runWith(groupedSink).onComplete(_ => system.terminate())

}
