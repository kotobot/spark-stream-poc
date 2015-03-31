name := "spark-stream-poc"

version := "1.0"

scalaVersion := "2.11.6"

val sparkVersion = "1.3.0"

libraryDependencies ++= Seq("org.apache.spark" % "spark-core_2.10" % sparkVersion % "provided",
														"org.apache.spark" % "spark-streaming_2.10" % sparkVersion % "provided",
														"org.apache.spark" % "spark-streaming-kafka_2.10" % sparkVersion
														//"com.typesafe.akka" %% "akka-actor" %
)
    