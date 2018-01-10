package com.lightbend.faq.streams

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by hungai on 13/10/2017.
  */
object WrapStreamInActor extends App {

  object PrintSomeNumber {
    def props(implicit mat: ActorMaterializer): Props = Props(new PrintSomeNumber)
  }

  class PrintSomeNumber(implicit mat: ActorMaterializer) extends Actor with ActorLogging {

    private implicit val ec = context.system.dispatcher

    override def preStart(): Unit = log.info("Starting the Printer Actor")

    override def postStop(): Unit = log.info("Stopping the Printer Actor")

    private val (killSwitch, done) =
      Source.tick(0 seconds, 1 second, 1)
        .scan(0) (_ + _)
        .map(_.toString)
        .viaMat(KillSwitches.single)(Keep.right)
        .toMat(Sink.foreach(println))(Keep.both)
        .run()

    done.map(_ => self ! "done")

    override def receive: Receive = {

      case "stop" =>
        println("Stopping")
        killSwitch.shutdown()
      case "done" =>
        println("Done")
        context.stop(self)
    }

  }

  implicit val system = ActorSystem("printerSystem")
  implicit val mat = ActorMaterializer()

  val printer = system.actorOf(PrintSomeNumber.props(mat))

  system.scheduler.scheduleOnce(10 seconds) {
    printer ! "stop"
  }

}
