package com.lightbend.faq.streams.alpakka.cassandra.csv

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge}

object Balancer {

  def create[In, Out](numberOfWorks: Int, worker: Flow[In, Out, Any]): Graph[FlowShape[In, Out], NotUsed] = GraphDSL.create() { implicit builder =>
    val balance = builder.add(Balance[In](numberOfWorks))
    val merge = builder.add(Merge[Out](numberOfWorks))

    import GraphDSL.Implicits._

    (1 to numberOfWorks).foreach(_ => balance ~> worker ~> merge)

    FlowShape(balance.in, merge.out)
  }

}
