package local.cwc.enhancedstats

import java.util.regex.{Matcher, Pattern}
import local.cwc.enhancedstats.Regex._
import local.cwc.enhancedstats.Maps._
import java.time._
import java.time.format._

object Parser {

  // Gets a raw line from the log and returns a map with all the fields in it
  def parseMainLog(rawLine:String) : Map[String, Any] = {
    // Construct a regular expression (regex) to extract fields from raw EnhancedStats log lines
    val mainPattern:Pattern = rawLineGetPattern()   // 1 Make Pattern object
    val matcher:Matcher = mainPattern.matcher(rawLine)    // 2 Match regex against line
      try {
        if (matcher.matches()) {
          val mainMap = getMainMap(matcher)
          val jsonMap = jsonStrToMap(mainMap("json").toString)
          (mainMap - "json") ++ jsonMap }
       else {
        // println(rawLine)
        // println(rawLineGetPattern())
        Map("parseMainLogError" -> rawLine)
    }}
      catch { case _: Throwable => Map("parseMainLogError" -> "some") }
  }

  // Returns a pattern to parse a raw line
  def rawLineGetPattern():Pattern = {
    // Fields
    val pubip = s"$ipv4"
    val module_name = s"(?:$word\\.?)*"
    val event_type = s"(?:$word\\.?)*"
    val timestamp = s"$int"
    val mac = s"$macaddress|n\\/a"
    val interface = s"$word|n\\/a"
    val ip = s"$ipv4|n\\/a"
    val sw_version = s"(?:$word[-\\.]?)*"
    val timezone = s"$word"
    val device_id = s"$word|n\\/a"
    val standby_state = s"$word|n\\/a"
    val mw_domain = s"(?:$word\\.?)*|n\\/a"
    val profile_id = s"$word|n\\/a"
    val rcu_id = s"$word"
    val last_context = s"$word"
    val action_source = s"$word"
    val json = s"\\{.+\\}"

    // Final Regex
    val regex = s"$word, \\[$timestamp_iso8601 #$word\\]  $word -- : ($pubip) -- \\[($module_name) ($event_type) ($timestamp) (?:($mac) " +
      s"($interface) ($ip) ($sw_version) ($timezone) ($device_id) ($standby_state) ($mw_domain) ($profile_id) ($rcu_id) ($last_context) ($action_source) )*JSON:($json)\\]"
    Pattern.compile(regex)
  }

  def indexName():String = {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    "bykey_enhanced_stats-" + date + "/logs"
  }

}
