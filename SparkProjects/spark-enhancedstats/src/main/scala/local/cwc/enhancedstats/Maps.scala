package local.cwc.enhancedstats

import java.util.regex.{Matcher, Pattern}
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import org.json4s.jackson.JsonMethods.parse


object Maps {

  def getFinalMap(agg:((String, String, String, String, String), Long)):Map[String, String] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val timestamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(formatter)
    Map[String, String](
      "event_type" -> agg._1._1,
      "mw_domain" -> agg._1._2,
      "sw_version" -> agg._1._3,
      "device_id" -> agg._1._4,
      "key" -> agg._1._5,
      "count" -> agg._2.toString,
      "timestamp" -> timestamp
    )
  }

  def getMainMap(m:Matcher): Map[String, Any] = {
    Map[String, String](
      "pubip" -> m.group(1),
      "module_name" -> m.group(2),
      "event_type" -> m.group(3),
      "timestamp" -> m.group(4),
      "mac" -> m.group(5),
      "interface" -> m.group(6),
      "ip" -> validateIP(m.group(7)),
      "sw_version" -> m.group(8),
      "timezone" -> m.group(9),
      "device_id" -> m.group(10),
      "standby_state" -> m.group(11),
      "mw_domain" -> m.group(12),
      "profile_id" -> m.group(13),
      "rcu_id" -> m.group(14),
      "last_context" -> m.group(15),
      "action_source" -> m.group(16),
      "json" -> jsonRemDB(m.group(17))
    )
  }

  def jsonStrToMap(jsonStr: String): Map[String, Any] = {
    implicit val formats = org.json4s.DefaultFormats
    val parsedJson = parse(jsonStr)
    val map = parsedJson.extract[Map[String, Any]]
    if (map.contains("duration") && map("duration").toString.toInt > 14400 &&
      map("duration").toString.toInt < 0) {
      val durNotOk = "durationnotok" -> map("duration")
      map + (durNotOk, "duration" -> 0)
    } else {
      map
    }
  }

  def jsonRemDB(json:String): String = {
    json.replaceAll("\\sdBm*","")
  }

  def validateIP(ip: String): String = {
    if (ip == "n/a" ) {
      return "0.0.0.0"
    }
    ip
  }
}
