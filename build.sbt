name := "spark-stream-poc"

version := "1.0"

scalaVersion := "2.10.4"

val sparkVersion = "1.3.0"

libraryDependencies ++= Seq("org.apache.spark" % "spark-core_2.10" % sparkVersion % "provided",
														"org.apache.spark" % "spark-streaming_2.10" % sparkVersion % "provided",
														"org.apache.spark" % "spark-streaming-kafka_2.10" % sparkVersion,
														"org.apache.spark" % "spark-sql_2.10" % sparkVersion
														//"com.typesafe.akka" %% "akka-actor" %
)

assemblyMergeStrategy in assembly := {
	case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
	case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
	case "log4j.properties"                                  => MergeStrategy.discard
	case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
	case "reference.conf"                                    => MergeStrategy.concat
	case _                                                   => MergeStrategy.first
}

    