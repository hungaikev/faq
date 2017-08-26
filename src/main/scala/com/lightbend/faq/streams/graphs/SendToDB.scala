package com.lightbend.faq.streams.graphs

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hungai on 26/08/2017.
  */
object SendToDB extends  App {


  object DB {
    case class Event(msg: String)

    def persistEvent(e: Event)(implicit ec: ExecutionContext): Future[Unit] = {
      println(s"persisting $e")
      Future {}
    }
  }



  implicit val system = ActorSystem("graph")
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()

  val intSource: Source[Int, NotUsed] = Source(1 to 100)

  val helloTimesTen: Flow[Int,String,NotUsed] = Flow[Int].map(i => s"Hello ${i * 10}")

  val intToEvent: Flow[Int,DB.Event,NotUsed] = Flow[Int].map(i => DB.Event(s"Event $i"))

  val printSink: Sink[Any,Future[Done]] = Sink.foreach(println)

  val dbSink = Flow[DB.Event].map(DB.persistEvent).toMat(Sink.ignore)(Keep.right).named("dbSink")

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))

      intSource ~> broadcast ~> helloTimesTen ~> printSink
                   broadcast ~> intToEvent ~> dbSink

      ClosedShape
    }
  )

  graph.run()

  system.terminate()

}



