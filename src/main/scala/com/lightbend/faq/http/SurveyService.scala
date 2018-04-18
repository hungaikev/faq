package com.lightbend.faq.http

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.server.directives.CachingDirectives._
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContextExecutor}
import scala.io.StdIn

trait Service {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

}

object SurveyService extends App with Service {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  val hostname = config.getString("http.host")
  val port = config.getInt("http.port")


  val simpleKeyer: PartialFunction[RequestContext, Uri] = {
    val isGet: RequestContext ⇒ Boolean = _.request.method == GET
    val isAuthorized: RequestContext ⇒ Boolean = _.request.headers.exists(_.is(Authorization.lowercaseName))
    PartialFunction {
      case r: RequestContext if isGet(r) && !isAuthorized(r) ⇒ r.request.uri
    }
  }


  val myCache = routeCache[Uri]

  var i = 0
  val route =
    cache(myCache, simpleKeyer) {
      complete {
        i += 1
        i.toString
      }
    }

  val server = Http().bindAndHandle(route, hostname, port)
  println(s"listening on $hostname:$port")

  StdIn.readLine()

  server.flatMap(_.unbind)
  system.terminate()
  println("terminated")

}
