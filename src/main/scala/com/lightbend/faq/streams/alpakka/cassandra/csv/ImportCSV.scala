package com.lightbend.faq.streams.alpakka.cassandra.csv

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object ImportCSV extends  App {

  implicit val system = ActorSystem("akka-streams")

  private val config = ConfigFactory.load()
  private val readingRepository = new ReadingRepository {}

  private val csvImporter = new CsvImporter(config,readingRepository)

  csvImporter.importFromFiles

}
