package org.example

import _root_.kafka.serializer.StringDecoder
import _root_.kafka.utils.Json
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.sql._
import org.example.model.Event

object Main {
  def main(args: Array[String]) = {

    val sparkConf = new SparkConf().setAppName("streamingPoc")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //ssc.checkpoint("checkpoint")

    val kafkaParams = Map(
      "metadata.broker.list" -> "localhost:9092"
    )


    val sqlContext = new SQLContext(ssc.sparkContext)

    import sqlContext.implicits._

    val dataset = ssc.sparkContext.makeRDD(Seq[(String, String)]())

    val stream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set("test"))
    stream.start()

    /*stream.foreachRDD { rdd =>
      println("Count = " + rdd.count())
      println("Content: \n" + rdd.collectAsMap())
    }*/
    println("Streaming started")
    stream.foreachRDD { batchRDD =>
      println("In foreach RDD loop")
      val united = dataset.union(batchRDD)
      val jsonRDD = sqlContext.jsonRDD(dataset.map(e => e._2))
      jsonRDD.registerTempTable("events")
      val rows = sqlContext.sql("SELECT * FROM events").collect()
      println("Query result: \n" + rows.map(r => r.mkString(", ")).mkString("\n"))
    }

    ssc.awaitTerminationOrTimeout(120000)
    println("Streaming finished")
  }

}