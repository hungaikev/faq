package com.lightbend.faq.http

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.caching.LfuCache
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.{ Uri, ContentTypes, StatusCodes, HttpResponse, HttpEntity }
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.CachingDirectives._
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.http.scaladsl.marshalling._
import akka.util.ByteString
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

trait Service {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

}

object SimpleService extends App with Service {
  type UserId = String
  case class User(id: UserId, lastName: String, created: Long)

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  val hostname = "localhost"
  val port = 8080

  def fetchUser(userId: UserId): Future[User] =
    Future.successful(User(userId, "Knap", System.currentTimeMillis))

  // Quick and dirty marshaller to make the example work, normally you would use e.g. JSON marshalling here.
  implicit val marshaller: ToResponseMarshaller[User] =
    Marshaller(ec => u => Future.successful(List(
      Marshalling.WithFixedContentType(
        ContentTypes.`text/plain(UTF-8)`,
        () => HttpResponse(entity = HttpEntity.Strict(contentType = ContentTypes.`text/plain(UTF-8)`, data = ByteString(u.toString.getBytes))))
    )))

  val myCache = LfuCache[UserId, User](system)

  val route: Route = {
    path("user" / Segment) { userId =>
      get {
        val user: Future[User] = myCache.getOrLoad(userId, fetchUser)
        complete(user)
      }
    }
  }

  val server = Http().bindAndHandle(route, hostname, port)
  println(s"listening on $hostname:$port")
}