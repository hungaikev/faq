package com.lightbend.faq.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

/**
  * Created by hungai on 29/08/2017.
  */
object Tweets extends App {

  implicit val system = ActorSystem("Tweets")
  implicit val mat = ActorMaterializer()

  implicit val ec = system.dispatcher

  final case class Author(handle: String)
  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] = body.split(" ").collect{
      case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]", " "))
    }.toSet
  }

  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
      Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
      Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
      Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
      Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
      Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
      Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
      Nil)


  val count: Flow[Tweet,Int,NotUsed] = Flow[Tweet].map(_ => 1)
  val sink: Sink[Int,Future[Int]] = Sink.fold[Int,Int](0)(_ + _)

  val counterGraph: RunnableGraph[Future[Int]] =
    tweets
    .via(count)
    .toMat(sink)(Keep.right)

  val sum: Future[Int] = counterGraph.run()

  sum.foreach(c => println(s"Total tweets processed: $c"))

  tweets
    .map(_.hashtags) // Get all sets of hashtags
    .reduce(_ ++ _) // Reduce them to a single set removing duplicates across all tweets
    .mapConcat(identity)
    .map(_.name.toUpperCase)
    .runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())

}
