package local.cwc.streamappeartvlogs

import local.cwc.streamappeartvlogs.Parser.{parseMainLog, getIndexName}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{StreamingContext, Seconds}
import org.elasticsearch.spark._
import java.time.LocalDate


object ATVLogMain {

  def main(args: Array[String]): Unit = {
    // Spark Conf
    val sc = new SparkConf()
      //.setMaster("local[*]")
      .setAppName("AppearTVLogs")
      .set("es.index.auto.create", "true")
      .set("es.nodes", "172.28.8.210:9200")
    //.set("es.mapping.id", id)

    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sc, Seconds(1))

    // Connecting to Kafka
    val brokers = "bbknode1:9092,bbknode2:9092"
    val groupId = "atvlogs"
    val topic = List("cwcatv").toSet
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "group.id" -> groupId,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topic, kafkaParams)
    )

    //val messages = ssc.socketTextStream("127.0.0.1", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    val requests = messages.map(line => {
      parseMainLog(line.value.toString.trim)
    })

    // Sending to ES
    requests.foreachRDD(rdd => rdd.saveToEs(getIndexName()))

    ssc.checkpoint("hdfs://172.28.8.211:8020/spark/appeartv")
    //ssc.checkpoint("/Users/olhernandez/Desktop/Spark")
    ssc.start()
    ssc.awaitTermination()
  }
}
