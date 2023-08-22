package local.cwc.enhancedstats

import local.cwc.enhancedstats.Parser.{parseMainLog, indexName}
import local.cwc.enhancedstats.Maps.getFinalMap
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{StreamingContext, Minutes}
import org.elasticsearch.spark._

object EnhancedStats {

  def main(args: Array[String]): Unit = {

    // Spark Conf
    val sc = new SparkConf()
      //.setMaster("local[*]")
      .setAppName("EnhancedStats")
      .set("es.index.auto.create", "true")
      .set("es.nodes", "172.28.8.210:9200")

    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sc, Minutes(5))

    // Connecting to Kafka
    val brokers = "172.28.8.211:9092,172.28.8.212:9092,172.28.8.213:9092," +
      "172.28.8.214:9092,172.28.8.215:9092,172.28.8.216:9092,172.28.8.217:9092," +
      "172.28.8.218:9092"
    val groupId = "enhancedstats"
    val topic = List("cwcenh").toSet
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

    val requests = messages.map(line => {
      parseMainLog(line.value.toString.trim)
    }).cache()

    // Aggregating by event_type, mw_domain.keyword, sw_version, device_id, key
    val keyAggr = requests.filter(filt => filt.contains("event_type") && filt("event_type") == "Notice.Input.KeyPressed")
      .map(map => (
        map("event_type").toString,
        map("mw_domain").toString,
        map("sw_version").toString,
        map("device_id").toString,
        map("key").toString))
      .countByValue()
      .reduceByKey(_+_)
      .map(x => getFinalMap(x))

    // Sending to ES
    keyAggr.foreachRDD(rdd => rdd.saveToEs(indexName()))

    // For testing purposes, use a local direcoty in your computer
    // For production, HDFS must be enabled
    ssc.checkpoint("hdfs://172.28.8.211:8020/spark/enhanced_stats")
    //ssc.checkpoint("/Users/olhernandez/Desktop/Spark")
    ssc.start()
    ssc.awaitTermination()

  }
}
