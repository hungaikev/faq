package com.lightbend.faq.streams.alpakka.cassandra.csv

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ReadingRepository {

  def save(reading: Reading): Future[Reading] = Future {
    reading
  }

}

