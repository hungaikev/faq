package com.lightbend.faq.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.duration._
import scala.io.StdIn


object StreamNumbers extends App {
  implicit val system = ActorSystem("number-system")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher



  val route =
    path("tasks") {
      get {
        complete {
          HttpResponse(entity =
            HttpEntity.Chunked.fromData(ContentTypes.`text/plain(UTF-8)`,
            Source.fromIterator(() => Iterator.iterate(0) (_ + 1))
              .map(i => s"This is task number: $i\n")
              .throttle(100, 1.second, 1, ThrottleMode.Shaping)
              .map {
                i => ByteString(i.toString)
              }
           )
          )
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

  println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
  StdIn.readLine()
  system.terminate()
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done

}
