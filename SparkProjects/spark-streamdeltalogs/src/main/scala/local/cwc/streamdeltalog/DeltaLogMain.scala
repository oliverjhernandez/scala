package local.cwc.streamdeltalog

import local.cwc.streamdeltalog.Parser.{getFinalMap, parseMainLog, getIndexName}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Minutes, StreamingContext}
import org.elasticsearch.spark._

object DeltaLogMain {

  def main(args: Array[String]): Unit = {

    // Spark Conf
    val sc = new SparkConf()
      .setMaster("local[*]")
      .setAppName("DeltaParser")
      .set("es.index.auto.create", "true")
      .set("es.nodes", "172.28.8.210:9200")

    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sc, Minutes(5))

    // Connecting to Kafka
    val brokers = "bbknode1:9092,bbknode2:9092"
    val groupId = "delta"
    val topic = List("cwcdelta").toSet
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
      ConsumerStrategies.Subscribe[String,String](topic, kafkaParams)
    )

    val requests = messages.map(line => {parseMainLog(line.value.toString.trim)})
      .filter(filt => filt.contains("fragment_type"))

    // Aggregating by "country", by "code", by "fragment_type", by "stream_name", by "bitrate"
    val sessionsPerStreamType = requests.filter(filt => filt("fragment_type").contains("v"))
      .map(map => (map("country_code"),map("status_code"),map("fragment_type"),map("stream_name"),map("bitrate")))
      .countByValue()
      .reduceByKey(_+_).map(x => getFinalMap(x)).print()

    // Sending to ES
    // sessionsPerStreamType.foreachRDD(rdd => rdd.saveToEs(getIndexName()))

    //ssc.checkpoint("hdfs://172.28.8.211:8020/spark/delta")
    ssc.checkpoint("/Users/olhernandez/Desktop/SparkRun")
    ssc.start()
    ssc.awaitTermination()
  }

}