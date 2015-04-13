package org.example

import java.time.Instant
import java.util.Date

import _root_.kafka.serializer.StringDecoder
import _root_.kafka.utils.Json
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.sql._
import org.example.model.Event

import scala.collection.mutable.ListBuffer

object Main {
  def main(args: Array[String]) = {

    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);

    val sparkConf = new SparkConf().setAppName("streamingPoc")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //ssc.checkpoint("checkpoint")

    val kafkaParams = Map(
      "zookeeper.connect" -> "localhost:2181",
      "group.id" -> "spark-streaming-poc",
      "zookeeper.connection.timeout.ms" -> "1000",
      "metadata.broker.list" -> "localhost:9092"
    )


    val sqlContext = new SQLContext(ssc.sparkContext)

    import sqlContext.implicits._

    val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set("test"))
    stream.print()

    var dataset: RDD[String] = ssc.sparkContext.emptyRDD[String]
    stream.foreachRDD(rdd =>
      dataset = dataset.union(rdd.map(_._2))
    )

    ssc.start()
    println("Streaming started")
    ssc.awaitTerminationOrTimeout(60000)

    val jsonRDD = sqlContext.jsonRDD(dataset)

    jsonRDD.registerTempTable("events")
    jsonRDD.printSchema()

    val rows = sqlContext.sql("SELECT * FROM events").collect()

    println("Query result: \n" + rows.map(r => r.mkString(", ")).mkString("\n"))
    println("Streaming finished")
  }

}