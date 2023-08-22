package local.cwc.streamappeartvlogs

import java.util.regex.{Matcher, Pattern}

import local.cwc.streamappeartvlogs.Regex._
import local.cwc.streamappeartvlogs.Maps._
import java.time.{LocalDate, LocalDateTime}
import java.time.format._
import java.util.Locale


object Parser {

  // Gets a raw line from the log and returns a map with all the fields in it
  def parseMainLog(rawLine:String) : Map[String, String] = {
    // Construct a regular expression (regex) to extract fields from raw ATS log lines
    if (rawLine.contains("ALARM_SET")) {
      val mainPattern:Pattern = logLineALARM_SETPattern()
      val matcher:Matcher = mainPattern.matcher(rawLine)
      if (matcher.matches()) {
        val mainMap = getALARM_SETMainMap(matcher)
        mainMap
      } else {
        //println(rawLine)
        Map("parseSETLogError" -> rawLine)
      }
    } else if (rawLine.contains("ALARM_CLR")) {
      val mainPattern:Pattern = logLineALARM_CLRPattern()
      val matcher:Matcher = mainPattern.matcher(rawLine)
      if (matcher.matches()) {
        //println(rawLine)
        val mainMap = getALARM_CLRMainMap(matcher)
        mainMap
      } else {
        //println(rawLine)
        Map("parseCLRLogError" -> rawLine)
      }
    } else {
      //println(rawLine)
      Map("unknownLog" -> rawLine)
    }
  }

  // Returns a pattern to parse a raw line
  def logLineALARM_SETPattern():Pattern = {

    // Fields
    val logdate = s"($month\\s*$monthday\\s$hour:$minute:$second)"
    val ip = s"($ipv4)"
    val stream = s"$word\\[($int)\\]:"
    val alarmtype = s"($word)"
    val array = s"@(\\[.*\\])"
    val message = "(.*):*"
    val channel = "(.*)(?:\\s\\(.*\\))?"

    // Final Regex
    val regex = s"$logdate $ip $stream $alarmtype $array $message $channel"
    //println(regex)
    Pattern.compile(regex)
  }

  def logLineALARM_CLRPattern():Pattern = {

    // Fields
    val logdate = s"($month\\s*$monthday\\s$hour:$minute:$second)"
    val ip = s"($ipv4)"
    val stream = s"$word\\[($int)\\]:"
    val alarmtype = s"($word)"
    val array = s"@(\\[.*\\])"
    val message = "(.*):*?"

    // Final Regex
    val regex = s"$logdate $ip $stream $alarmtype $array $message"
    Pattern.compile(regex)
  }

  def parseDate(date:String):String = {
    val year:String = LocalDate.now().getYear.toString
    val formatter = DateTimeFormatter.ofPattern("MMM [ ]d HH:mm:ss yyyy" ).withLocale(Locale.ENGLISH)
    LocalDateTime.parse(date + s" $year", formatter).toString
  }

  def getIndexName():String = {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    "appeartv_logs-" + date + "/appeartv_logs"
  }
}
