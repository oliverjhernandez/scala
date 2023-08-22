lazy val root = (project in file(".")).
  settings(
    name := "spark-streamatsvspplog",
    version := "0.1",
    scalaVersion := "2.11.8",
    organization := "local.cwc",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka-common" % "0.10.0.1",
      "org.apache.spark" %% "spark-core" % "2.3.0",
      "org.apache.spark.streaming" %% "spark-streaming-kafka" % "0-10_2.11-2.3.0",
      "org.apache.spark.streaming" %% "spark-streaming" % "2.3.0",
      "org.elasticsearch.spark" %% "elasticsearch-spark" % "5.6.8"
    )
  )

