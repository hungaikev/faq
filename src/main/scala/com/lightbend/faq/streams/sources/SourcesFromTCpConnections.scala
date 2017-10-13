package com.lightbend.faq.streams.sources

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Tcp}
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object SourcesFromTCpConnections extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher


  /**
    * Tcp().bind -> Creates a Source from a TCP connection
    *            -> Each time a client connects a new connection will be emitted to the stream
    *            -> Connections can be processed by attaching a Flow to the connection
    */

  val connections : Source[IncomingConnection, Future[ServerBinding]] =
    Tcp().bind("127.0.0.1", 8888)

  connections.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())


}
