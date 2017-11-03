package com.lightbend.faq.streams.flows

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object FlowsToGroupElements extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val source: Source[Int, NotUsed] = Source.unfold(0) {
    case value if value <= 100 => Some((value + 1, value))
    case _ => None
  }

  val sink: Sink[Seq[Int], Future[Done]] = Sink.foreach(i => println(s"Here is the grouping $i"))

  /**
    * Flow.grouped -> Group elements in the stream into fixed size batches
    */

  val groupsOf10: Flow[Int, Seq[Int],NotUsed] = Flow[Int].grouped(10)

  /**
    * Flow.sliding -> Creates a sliding window over the element in the stream.
    */

  val slidingWindowOf10: Flow[Int, Seq[Int], NotUsed] = Flow[Int].sliding(10, step = 1)

  source.via(slidingWindowOf10).runWith(sink).onComplete(_ => system.terminate())

}
