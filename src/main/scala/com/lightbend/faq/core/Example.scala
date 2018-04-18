package com.lightbend.faq.core

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}

object Example extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val bufferSize = 100

  case class Elem(substream: Int)(val f: () => Unit)

  val queue = Source
    .queue[Elem](bufferSize, OverflowStrategy.backpressure)
    .groupBy(10, _.substream)
    .async // make sure each sub stream runs in its own actor
    .buffer(bufferSize, OverflowStrategy.backpressure)
    .map { _.f() }
    .to(Sink.ignore)
    .run()

  queue.offer(Elem(1) { () => Thread.sleep(5000); println(1) })
  queue.offer(Elem(1) { () => println(2) })
  queue.offer(Elem(2) { () => println(3) })

  // expected: 3, 1, 2
  // actual: output 1, 2, 3
}