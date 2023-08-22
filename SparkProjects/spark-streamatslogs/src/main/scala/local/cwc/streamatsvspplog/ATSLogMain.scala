package local.cwc.streamatsvspplog

import local.cwc.streamatsvspplog.Parser.parseMainLog
import local.cwc.streamatsvspplog.Maps.getFinalMap
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{StreamingContext, Minutes}
import org.elasticsearch.spark._
import java.time.LocalDate

object ATSLogMain {

  def main(args: Array[String]): Unit = {

    // Spark Conf
    val sc = new SparkConf()
      //.setMaster("local[*]")
      .setAppName("ATSVSPPLogParser")
      .set("es.index.auto.create", "true")
      .set("es.nodes", "172.28.8.210:9200")
    //.set("es.mapping.id", id)


    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sc, Minutes(5))

    // Connecting to Kafka
    val brokers = "bbknode1:9092,bbknode2:9092"
    val groupId = "vsppapollos"
    val topic = List("cwcats-vspp").toSet
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

    val requests = messages.map ( line => {
      parseMainLog(line.value.toString.trim)
    })

    // Aggregating by "chi", by "stream_name", by "stream_type", by "phi"
    val sessionsPerStreamType = requests.filter(filt => filt.contains("fragment_type"))
      .filter(filt => filt("fragment_type").contains("vid"))
      .map(map => (map("chi"),map("stream_name"),map("stream_type"),map("phn")))
      .countByValue()
      .filter( filt => filt._2 > 5)
      .reduceByKey(_+_)
      .map(map => getFinalMap(map))


    // Sending to ES
    val date = LocalDate.now().toString
    val index = "ats_concurrent_sessions-" + date + "/ats_concurrent_sessions"
    sessionsPerStreamType.foreachRDD(rdd => rdd.saveToEs(index))

    ssc.checkpoint("hdfs://172.28.8.211:8020/spark/vsppapollos")
    //ssc.checkpoint("/Users/olhernandez/Desktop/SparkRun")
    ssc.start()
    ssc.awaitTermination()
  }
}
