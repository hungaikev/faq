package com.lightbend.faq.streams.sources

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future

/**
  * Created by hungai on 13/10/2017.
  */
object SourcesFromFiles extends App {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  /**
    * FileIO.fromPath -> Creates a Source of ByteString from a file using a thread-pool backed dispatcher dedicated for FileIO
    *                 -> Pulls data from the file and pushed it down stream whenever there is demand
    *                 -> Completes when the end of teh file is reached.
    *                 -> Use the FramingAPI and a decoder to parse ByteString into lines of text.
    */

  val byteSource: Source[ByteString, Future[IOResult]] =
    FileIO.fromPath(
      Paths.get("src/main/resources/logfile.txt"),
      chunkSize = 1024
    )

  byteSource.runWith(Sink.foreach(println))
    .onComplete(_ => system.terminate())


}
