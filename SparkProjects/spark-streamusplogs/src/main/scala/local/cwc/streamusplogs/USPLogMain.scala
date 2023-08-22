package local.cwc.streamusplogs

import local.cwc.streamusplogs.Parser.{parseMainLog, getFinalMap, getIndexName}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Minutes, StreamingContext, Seconds}
import org.elasticsearch.spark._

object USPLogMain {

  def main(args: Array[String]): Unit = {

    // Spark Conf
    val sc = new SparkConf()
        .setMaster("local[*]")
        .setAppName("USPLogs")
        .set("es.index.auto.create", "true")
        .set("es.nodes", "172.28.8.210:9200")
        .set("es.http.retries", "5000")

    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sc, Seconds(10))

    // Connecting to Kafka
    val brokers = "172.28.8.211:9092,172.28.8.212:9092,172.28.8.213:9092," +
      "172.28.8.214:9092,172.28.8.215:9092,172.28.8.216:9092,172.28.8.217:9092," +
      "172.28.8.218:9092"
    val groupId = "usporigins"
    val topic = List("cwcusp").toSet
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "group.id" -> groupId,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    // Getting messages from Kafka
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](topic, kafkaParams)
    )

    // Parsing
    val requests = messages.map ( line => {
      parseMainLog(line.value.toString.trim)
    }).filter(filt => filt.contains("fragment_type"))
      .filter(filt => filt("fragment_type").contains("v")).cache()

    // Aggregating VOD by "country", by "code", by "fragment_type", by "asset_type", by "quality"
    val vodAggr = requests.filter(filt => filt("request").contains("vod"))
      .map(map => (
        map("asset_type"),
        map("code"),
        map("fragment_type"),
        map("quality"),
        map("stream_type"),
        map("usporigin")))
      .countByValue()
      .reduceByKey(_+_)
      .map(x => getFinalMap(x))

    // Aggregating LIVE by "country", by "code", by "fragment_type", by "stream_name", by "quality"
    val liveAggr = requests.filter(filt => filt("request").contains("live"))
      .map(map => (
        map("stream_name"),
        map("code"),
        map("fragment_type"),
        map("quality"),
        map("stream_type"),
        map("usporigin")))
      .countByValue()
      .reduceByKey(_+_)
      .map(x => getFinalMap(x))

    // Aggregating NPVR by "country", by "code", by "fragment_type", by "stream_name", by "quality"
    val npvrAggr = requests.filter(filt => filt("request").contains("npvr"))
      .map(map => (
        map("stream_name"),
        map("code"),
        map("fragment_type"),
        map("quality"),
        map("stream_type"),
        map("usporigin")))
      .countByValue()
      .reduceByKey(_+_)
      .map(x => getFinalMap(x))

    val finalRDD = vodAggr.union(liveAggr).union(npvrAggr).print(20)

    // Sending to ES
    //finalRDD.foreachRDD(rdd => rdd.saveToEs(getIndexName()))

    ssc.checkpoint("hdfs://172.28.8.211:8020/spark/usporigins")
    //ssc.checkpoint("/Users/olhernandez/Desktop/Spark")
    ssc.start()
    ssc.awaitTermination()
  }
}