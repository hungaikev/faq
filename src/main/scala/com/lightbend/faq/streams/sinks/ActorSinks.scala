package com.lightbend.faq.streams.sinks

import akka.{Done, NotUsed}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object ActorSinks extends App {


  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()


  val countTo100: Source[Int,NotUsed] = Source.unfold(0){
    case value if value <= 10000 => Some((value+1, value))
    case _ => None
  }

  case object PrintSum

  val sumActor = system.actorOf(Props(
    new Actor with ActorLogging {

      private var sum = 0
      override def preStart(): Unit = log.info("Starting PrintSum actor ")

      override def postStop(): Unit = log.info("Stopping PrintSum actor ")

      override def receive: Receive = {
        case value: Int => sum += value
        case PrintSum => log.info("The sum is {}", sum.toString())
      }
    }
  ))


  val computeSum: Sink[Int, NotUsed] = Sink.actorRef[Int](sumActor,onCompleteMessage = PrintSum)

  countTo100.runWith(computeSum)


}
