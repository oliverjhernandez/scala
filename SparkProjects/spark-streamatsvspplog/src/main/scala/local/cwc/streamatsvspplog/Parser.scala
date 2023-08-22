package local.cwc.streamatsvspplog

import java.util.regex.{Matcher, Pattern}
import local.cwc.streamatsvspplog.Regex._
import local.cwc.streamatsvspplog.Maps._
import java.time._
import java.time.format._
import java.util.Locale

object Parser {

  // Gets a raw line from the log and returns a map with all the fields in it
  def parseMainLog(rawLine:String) : Map[String, String] = {
    // Construct a regular expression (regex) to extract fields from raw ATS log lines
    val mainPattern:Pattern = rawLineGetPattern()   // 1 Make Pattern object
    val matcher:Matcher = mainPattern.matcher(rawLine)    // 2 Match regex against line
    if (matcher.matches()) {
      val mainMap = getMainMap(matcher)
      val cquucMap = cquucGetMap(mainMap("cquuc"))
      mainMap ++ cquucMap
    } else {
      Map("parseMainLogError" -> rawLine)
    }
  }

  def parseApacheDate(apacheDate:String):String = {
    val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z").withLocale(Locale.ENGLISH)
    ZonedDateTime.parse(apacheDate, formatter).toString
  }

  // Returns a pattern to parse a raw line
  def rawLineGetPattern():Pattern = {
    // Fields
    val chi = s"^($ipv4)"
    val dash = "-"
    val caun = s"($user)"
    val cqtn = "\\[(" + httpdate + ")\\]"
    val cqtx = "\"" + word + s" ($uri)" + s" $word" + "\\/" + number + "\""
    val pssc = s"($word)"
    val pscl = s"($int)"
    val sssc = s"($int)"
    val sscl = s"($int)"
    val cqbl = s"($int)"
    val pqbl = s"($int)"
    val cqhl = s"($int)"
    val pshl = s"($int)"
    val pqhl = s"($int)"
    val sshl = s"($int)"
    val tts = s"($int)"
    val phr = s"($word)"
    val cfsc = s"($word)"
    val pfsc = s"($word)"
    val crc = s"($word)"
    val cquuc = s"($uri)"
    val phi = s"($ipv4)"
    val shn = s"($iporhost)"
    val stms = s"($int)"
    val phn = s"($iporhost)"

    // Final Regex
    val regex = s"$chi $dash $caun $cqtn $cqtx $pssc $pscl $sssc $sscl $cqbl $pqbl $cqhl $pshl $pqhl $sshl $tts $phr $cfsc $pfsc $crc $cquuc $phi $shn $stms $phn"
    Pattern.compile(regex)
  }

  def getIndexName():String = {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    "ats_concurrent_sessions-" + date + "/ats_concurrent_sessions"
  }
}
