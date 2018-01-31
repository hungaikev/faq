package com.lightbend.faq.streams.alpakka.cassandra.csv

import scala.util.Random

trait Reading {

  def id: Int

}

case class ValidReading(id: Int, value: Double = Random.nextDouble()) extends Reading
case class InvalidReading(id: Int) extends Reading