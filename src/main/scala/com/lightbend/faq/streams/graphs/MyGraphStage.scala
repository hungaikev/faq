package com.lightbend.faq.streams.graphs

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString

/**
  * Created by hungai on 02/10/2017.
  */
object MyGraphStage {

  sealed trait ParseEvent
  sealed trait TextEvent extends ParseEvent {
    def text: String
  }

  case object StartDocument extends ParseEvent
  case object EndDocument extends ParseEvent
  final case class StartElement(localName: String, attributes: Map[String,String]) extends ParseEvent
  final case class EndElement(localName: String) extends ParseEvent
  final case class Characters(text: String) extends TextEvent
  final case class ProcessingInstruction(target: Option[String], date: Option[String]) extends ParseEvent
  final case class Comment(text: String) extends ParseEvent
  final case class CData (text: String) extends TextEvent


  //A stage is aways an instance of a GraphStage with a certain Shape.

  class StreamingXmlParser extends GraphStage[FlowShape[ByteString,ParseEvent]] {

    // The input port to consume ByteString from
    val in: Inlet[ByteString] = Inlet("XMLParser.in")

    //The output port to emit parsed events to

    val out: Outlet[ParseEvent] = Outlet("XMLParser.out")

    //Since we have only one input and output, we have a FlowShape

    override val shape: FlowShape[ByteString,ParseEvent] = FlowShape(in,out)

    // Never put mutable state here
    // all such state must go into the GraphStageLogic

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) with InHandler with OutHandler {

        // Add Stateful XML parser here

        override def onPush(): Unit = ???

        override def onPull(): Unit = ???
    }

  }

}
