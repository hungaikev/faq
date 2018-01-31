package com.lightbend.faq.streams.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.duration._

object ProcessingKafkaApplication extends App {
  implicit val system = ActorSystem("kafka")
  implicit val mat = ActorMaterializer()

  val bootstrapServer = "localhost: 9092"
  val kafkaTopic = "akka_streams_topic"
  val partition = 0
  val subscription = Subscriptions.assignment(new TopicPartition(kafkaTopic,partition))

  val consumerSettings = ConsumerSettings(system,
    new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServer)
    .withGroupId("akka_streams_group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest")

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServer)


  val runnableGraph = RunnableGraph.fromGraph(GraphDSL.create(){ implicit builder =>
    import GraphDSL.Implicits._
    val tickSource = Source.tick(0 seconds, 5 seconds, "Hello from akka streams")
    val kafkaSource = Consumer.plainSource(consumerSettings, subscription)
    val kafkaSink = Producer.plainSink(producerSettings)
    val printlnSink = Sink.foreach(println)

    val mapToProducerRecord = Flow[String].map(elem =>
      new ProducerRecord[Array[Byte],String](kafkaTopic,elem)
    )

    val mapFromConsumerRecord = Flow[ConsumerRecord[Array[Byte],String]].map(record => record.value())

    tickSource ~> mapToProducerRecord ~> kafkaSink
    kafkaSource ~> mapFromConsumerRecord ~> printlnSink

    ClosedShape
  })

  runnableGraph.run()

}
