package local.cwc.streamusplogs

import java.time._
import java.time.format._
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.regex.{Matcher, Pattern}

import local.cwc.streamusplogs.Maps._
import local.cwc.streamusplogs.Regex._

object Parser {

  // Gets aggregation and returns a map
  def getFinalMap(agg:((String, String, String, String, String, String),Long)):Map[String, String] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val timestamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(formatter)
    Map[String, String](
      "asset" -> agg._1._1,
      "status_code" -> agg._1._2,
      "fragment_type" -> agg._1._3,
      "quality" -> agg._1._4,
      "stream_type" -> agg._1._5,
      "usporigin" -> agg._1._6,
      "chunks" -> agg._2.toString,
      "timestamp" -> timestamp,
      "country_code" -> defineCountry(agg._1._6)
    )
  }

  // Gets a raw line from the log and returns a map with all the fields in it
  def parseMainLog(rawLine:String) : Map[String, String] = {
    // Construct a regular expression (regex) to extract fields from raw ATS log lines
    val mainPattern:Pattern = rawLineGetPattern()   // 1 Make Pattern object
    val matcher:Matcher = mainPattern.matcher(rawLine)    // 2 Match regex against line
    if (matcher.matches()) {
      val mainMap = getMainMap(matcher)
      val requestMap = requestGetMap(mainMap("request"))
      mainMap ++ requestMap

    } else {
      //println(rawLine)
      //println(mainPattern)
      Map("parseMainLogError" -> rawLine)
    }
  }

  // Returns a pattern to parse a raw line
  def rawLineGetPattern():Pattern = {
    // Fields
    val client = s"^($ipv4)"
    val dash = "-"
    val auth = s"($user)"
    val date = "\\[(" + httpdate + ")\\]"
    val request = "\"" + s"($word) " + s"($uripathparam) " + s"$word" + "\\/" + s"$number" + "\""
    val code = s"($word)"
    val size = s"($int|-)"
    val referer = "\"" + s"($word|-|$uri)" + "\""
    val agent = "\"" + s"($anything)" + "\""
    val forwarded = "\"(" + s"(?:$ipv4[,\\s]*)*|-" + ")\""
    val spectrum = "\"" + s"($word|-)" + "\""
    val usporigin = s"($ipv4)"

    // Final Regex
    val regex = s"$client $dash $auth $date $request $code $size $referer $agent $forwarded $spectrum $usporigin"
    Pattern.compile(regex)
  }

  def parseApacheDate(apacheDate:String):String = {
    val formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy:HH:mm:ss Z").withLocale(Locale.ENGLISH)
    ZonedDateTime.parse(apacheDate, formatter).toString
  }

  def parseApacheSize(apacheSize:String):String = {
    if (apacheSize == "-") {
      "0"
    } else {
      apacheSize.toString
    }
  }

  def defineCountry(ipaddress:String):String = {
    ipaddress match {
      case ip if ip.contains("172.28.5") => "ecp"
      case ip if ip.contains("172.26.113") => "pa"
      case _ => ipaddress
    }
  }

  def getIndexName():String = {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    "usp_origin_logs-" + date + "/usp"
  }

}