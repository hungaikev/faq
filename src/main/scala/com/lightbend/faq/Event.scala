package com.lightbend.faq

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}



/**
  * Created by hungai on 05/09/2017.
  */

object Event extends App {

  case class CreateTicket(id: String)
  case class DeleteTicket(id: String)


  object TicketMaster {

    def props: Props = Props(new TicketMaster)

  }

  class TicketMaster extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("TicketMaster Created")

    override def postStop(): Unit = log.info("TicketMaster Stopped")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      println("preRestart happening")
      super.preRestart(reason,message)}

    override def receive: Receive = {
      case CreateTicket(id) =>
        val ticket = context.actorOf(Ticket.props)
        // ticket ! DeleteTicket(id)
    }
  }


  object Ticket {
    def props: Props = Props(new Ticket)
  }

  class Ticket extends Actor with ActorLogging {
    override def preStart(): Unit = log.info("I have been started by my master because I  am a ticket")

    override def postStop(): Unit = log.info("I have to die before my master because I am a ticket")

    override def receive: Receive = {
      case DeleteTicket(id) =>
        self ! PoisonPill
    }
  }

  val system = ActorSystem("testing")

  val ticketMaster = system.actorOf(TicketMaster.props)

  ticketMaster ! CreateTicket("Nindo")
  // ticketMaster ! PoisonPill
  system.terminate()

}