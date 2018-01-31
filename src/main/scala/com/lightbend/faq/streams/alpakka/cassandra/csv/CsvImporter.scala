package com.lightbend.faq.streams.alpakka.cassandra.csv

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.stream.scaladsl.{Flow, Framing, Keep, Sink, Source, StreamConverters}
import akka.util.ByteString
import com.typesafe.config.Config

import scala.concurrent.Future
import scala.util.{Failure, Success}

class CsvImporter(config: Config,readingRepository: ReadingRepository) (implicit val system : ActorSystem) {

  implicit val materializer = ActorMaterializer()

  val logger = Logging(system, getClass)

  import system.dispatcher

  private val importDirectory = Paths.get(config.getString("importer.import-directory")).toFile
  private val linesToSkip = config.getInt("importer.lines-to-skip")
  private val concurrentFiles = config.getInt("importer.concurrent-files")
  private val concurrentWrites = config.getInt("importer.concurrent-writes")
  private val nonIOParallelism = config.getInt("importer.non-io-parallelism")

  def parseLine (filePath: String)(line: String): Future[Reading] = Future {
    val fields = line.split(";")
    val id = fields(0).toInt

    try {
      val value = fields(1).toDouble
      ValidReading(id, value)
    } catch {
      case t: Throwable => logger.error(s"Unable to parse line in $filePath:\n $line! ${t.getMessage}")
        InvalidReading(id)
    }
  }


  val lineDelimiter: Flow[ByteString,ByteString, NotUsed] = Framing.delimiter(ByteString("\n"), 128, allowTruncation = true)

  val parseFile: Flow[File, Reading, NotUsed] = Flow[File].flatMapConcat{ file =>
    val inputStream = new GZIPInputStream(new FileInputStream(file))

    StreamConverters.fromInputStream(() => inputStream)
      .via(lineDelimiter)
      .drop(linesToSkip)
      .map(_.utf8String)
      .mapAsync(nonIOParallelism)(parseLine(file.getPath))
  }


  val computeAverage: Flow[Reading,ValidReading, NotUsed] = Flow[Reading].grouped(2).mapAsyncUnordered(nonIOParallelism) { readings =>
    Future{
      val validReadings = readings.collect {
        case r: ValidReading => r }
      val average = if(validReadings.nonEmpty) validReadings.map(_.value).sum / validReadings.size else -1
      ValidReading(readings.head.id, average)
    }
  }

  val storeReadings: Sink[ValidReading, Future[Done]] =
    Flow[ValidReading].mapAsyncUnordered(concurrentWrites)(readingRepository.save).toMat(Sink.ignore)(Keep.right)

  val processSingleFile: Flow[File, ValidReading, NotUsed] = Flow[File].via(parseFile).via(computeAverage)


  def importFromFiles: Future[Done] = {
    val files = importDirectory.listFiles().toList
    logger.info(s"Starting import of ${files.size} files from ${importDirectory.getPath} Directory")
    val startTime = System.currentTimeMillis()

    Source(files)
      .via(Balancer.create(concurrentFiles, processSingleFile))
      .withAttributes(ActorAttributes.supervisionStrategy { e =>
        logger.error("Exception thrown during stream processing", e)
        Supervision.Resume
      })
      .runWith(storeReadings)
      .andThen{
        case Success(_) =>
          val elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0
          logger.info(s"Import finished in ${elapsedTime}")
        case Failure(e) =>
          logger.error("Import failed")
      }
  }

}
