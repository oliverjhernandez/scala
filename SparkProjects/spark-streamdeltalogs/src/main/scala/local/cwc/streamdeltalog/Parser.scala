package local.cwc.streamdeltalog

import java.time._
import java.time.format._
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.regex.{Matcher, Pattern}

import local.cwc.streamdeltalog.Maps._
import local.cwc.streamdeltalog.Regex._

object Parser {

  // Gets aggregation and returns a map
  def getFinalMap(agg:((String, String, String, String, String),Long)):Map[String, String] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val timestamp = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(formatter)
    Map[String, String](
      "country_code" -> agg._1._1,
      "status_code" -> agg._1._2,
      "fragment_type" -> agg._1._3,
      "stream_name" -> agg._1._4,
      "bitrate" -> agg._1._5,
      "chunks" -> agg._2.toString,
      "timestamp" -> timestamp
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
      // println()
      mainMap ++ requestMap
    } else {
      // println(rawLine)
      // println(mainPattern)
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
    val reqid = s"(?:\\[(?:.*)\\]\\s)?"
    val request = "\"" + s"($word) " + s"($uripath) " + s"$word" + "\\/" + s"$number" + "\""
    val code = s"($word)"
    val size = s"($int|-)"
    val time = s"($int|-)"

    // Final Regex
    val regex = s"$client $dash $auth $date $reqid$request $code $size $time"
    // println(regex)
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

  def bitrateCalc(bitrate:Int):String = {
    bitrate match {
      case bit if bit > 4000000 && bit <= 4500000 => "4500000"
      case bit if bit > 3000000 && bit <= 4000000 => "4000000"
      case bit if bit > 2200000 && bit <= 3000000 => "3000000"
      case bit if bit > 1500000 && bit <= 2200000 => "2200000"
      case bit if bit > 1200000 && bit <= 1500000 => "1500000"
      case bit if bit > 800000 && bit <= 1200000 => "1200000"
      case bit if bit > 500000 && bit <= 800000 => "800000"
      case bit if bit > 300000 && bit <= 500000 => "500000"
      case bit if bit > 2000000 && bit <= 300000 => "300000"
      case bit if bit > 1500000 && bit <= 2000000 => "2000000"
      case bit if bit > 800000 && bit <= 1500000 => "1500000"
      case bit if bit > 600000 && bit <= 800000 => "800000"
      case bit if bit > 300000 && bit <= 600000 => "600000"
      case bit if bit <= 300000 => "300000"
      case _ => bitrate.toString
    }
  }

  def defineCountry(ipaddress:String):String = {
    ipaddress match {
      case ip if ip.contains("172.26.97") => "bb"
      case ip if ip.contains("172.26.109") => "bs"
      case ip if ip.contains("172.26.111") => "ai"
      case ip if ip.contains("172.26.175") => "kn"
      case ip if ip.contains("172.26.121") => "vg"
      case ip if ip.contains("172.28.13") => "an"
      case ip if ip.contains("172.26.29") => "jm"
      case ip if ip.contains("172.26.41") => "tt"
      case ip if ip.contains("172.26.169") => "jmcl"
      case ip if ip.contains("172.26.171") => "jmmb"
      case ip if ip.contains("172.26.107") => "ky"
      case ip if ip.contains("172.26.177") => "dm"
      case ip if ip.contains("172.26.113") => "pa"
      case ip if ip.contains("172.26.161") => "lc"
      case ip if ip.contains("172.26.105") => "tci"
      case _ => ipaddress
    }
  }

  def getIndexName():String = {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    "elemental_delta-" + date + "/delta"
  }
}