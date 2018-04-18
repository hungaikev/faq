package com.lightbend.faq.streams


import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{ Message, TextMessage, WebSocketRequest }
import akka.stream._
import akka.stream.scaladsl.{ BroadcastHub, Flow, Keep, Sink, Source }

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Server extends App {

  import akka.http.scaladsl.server.Directives

  implicit val system = ActorSystem("akka_system")
  implicit val flowMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val interface = "localhost"
  val port = 9999

  import Directives._

  val route_hello = get {
    pathEndOrSingleSlash {
      complete("Wow, such networking")
    }
  }

  val intSource = Source(1 to 1000).throttle(1, 1.seconds, 1, ThrottleMode.shaping).map(_.toString)
  val intBroadcastedSource = intSource.toMat(BroadcastHub.sink(bufferSize = 256))(Keep.right).run()

  val sourceProjects = Source.actorRef[String](10, OverflowStrategy.fail)
  // val ref = sourceProjects.to(Sink.ignore).run()
  val (ref, projectsBroadcastedSource) = sourceProjects
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both).run()

  val source = projectsBroadcastedSource

  val handle: Flow[Message, Message, NotUsed] =
    Flow[Message]
      .via(Flow.fromSinkAndSource(Sink.ignore, source))
      .map[Message](s ⇒ TextMessage(s))

  def echo(message: String) = {
    ref ! message
  }

  val route_ping = path("ping") {
    get {
      echo("pong")
      complete("ok")
    }
  }

  val route_websocket = (get & path("websocket")) {
    handleWebSocketMessages(handle)
  }

  val allRoutes = route_hello ~ route_websocket ~ route_ping

  val binding = Http().bindAndHandle(allRoutes, interface, port)
  println(s"Project server is running on $interface:$port")

  Await.result(binding, 10.seconds)

  private val request = WebSocketRequest(s"ws://${interface}:${port}/websocket")

  // one websocket client:
  Http().singleWebSocketRequest(request, Flow.fromSinkAndSource(
    Sink.foreach[Message](el ⇒ println("client 1 received: " + el)),
    Source.single("Hi!").map(t ⇒ TextMessage(t))
  ))

  // second websocket client:
  Http().singleWebSocketRequest(request, Flow.fromSinkAndSource(
    Sink.foreach[Message](el ⇒ println("client 2 received: " + el)),
    Source.single("Hi!").map(t ⇒ TextMessage(t))
  ))

  StdIn.readLine()

  println(s"Shutting down Project server...")
  binding.flatMap(_.unbind()).onComplete(_ ⇒ system.terminate())
  println(s"Project server is shut down")
}