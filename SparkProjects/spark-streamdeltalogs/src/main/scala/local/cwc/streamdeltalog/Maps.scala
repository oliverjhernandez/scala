package local.cwc.streamdeltalog

import java.util.regex.{Matcher, Pattern}

import local.cwc.streamdeltalog.Parser.{parseApacheDate, parseApacheSize, defineCountry}
import local.cwc.streamdeltalog.Regex._

object Maps {

  def getMainMap(m:Matcher): Map[String, String] = {

    Map[String, String](
      "country_code" -> defineCountry(m.group(1)),
      "auth" -> m.group(2),
      "date" -> parseApacheDate(m.group(3)),
      "method" -> m.group(4),
      "request" -> m.group(5),
      "status_code" -> parseApacheSize(m.group(6)),
      "bitrate" -> m.group(7),
      "reqtime" -> m.group(8)
    )
  }

  def getRequestMapManif(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "encrypted" -> m.group(2),
      "stream_name" -> m.group(1),
      "streaming_file_ext" -> m.group(3)
    )
  }

  def getRequestMapChunk(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "encrypted" -> m.group(1),
      "stream_name" -> m.group(2),
      "streaming_file_ext" -> m.group(3),
      "bitrate" -> m.group(4),
      "fragment_type" -> m.group(5).charAt(0).toString,
      "time_reference" -> m.group(6)
    )
  }

  def requestGetMap( request:String ):Map[String, String] = {
    // Based on the url, we get a regex pattern and immediately
    // parse the "request" field which is the entry for the getMaps functions
    if (request.contains("Manifest")) {
      val mainPattern: Pattern = Pattern.compile(manifpath)
      val m: Matcher = mainPattern.matcher(request)
      if (m.matches()) {
        getRequestMapManif(m)
      } else {
        //println(manifpath)
        Map("RegexError" -> request)
      }
    } else if (request.contains("QualityLevels")) {
      val mainPattern: Pattern = Pattern.compile(chunkpath)
      val m: Matcher = mainPattern.matcher(request)
      if (m.matches()) {
        getRequestMapChunk(m)
      } else {
        //println(chunkpath)
        Map("RegexError" -> request)
      }
    } else {
      //println(manifpath)
      Map("MatcherError" -> request)
    }
  }
}