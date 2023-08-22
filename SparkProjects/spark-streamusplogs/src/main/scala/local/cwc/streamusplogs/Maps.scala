package local.cwc.streamusplogs

import java.util.regex.{Matcher, Pattern}

import local.cwc.streamusplogs.Parser.{parseApacheDate, parseApacheSize}
import local.cwc.streamusplogs.Regex._

object Maps {

  def getMainMap(m:Matcher): Map[String, String] = {
    Map[String, String](
      "client" -> m.group(1),
      "auth" -> m.group(2),
      "date" -> parseApacheDate(m.group(3)),
      "command" -> m.group(4),
      "request" -> m.group(5),
      "code" -> m.group(6),
      "size" -> parseApacheSize(m.group(7)),
      "referer" -> m.group(8),
      "agent" -> m.group(9),
      "forwarded" -> m.group(10),
      "spectrum" -> m.group(11),
      "usporigin" -> m.group(12)
    )
  }

  def getRequestMapManifNpvr(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "stream_type" -> m.group(1),
      "mount_point" -> m.group(2),
      "stream_name" -> m.group(3),
      "asset_id" -> m.group(4),
      "device_type" -> m.group(5),
      "streaming_file_ext" -> m.group(6),
      "vbegin" -> m.group(6),
      "vend" -> m.group(7)
    )
  }

  def getRequestMapChunkNpvr(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "stream_type" -> m.group(1),
      "mount_point" -> m.group(2),
      "stream_name" -> m.group(3),
      "asset_id" -> m.group(4),
      "device_type" -> m.group(5),
      "streaming_file_ext" -> m.group(6),
      "quality" -> m.group(7),
      "fragment_type" -> m.group(8).substring(0,1),
      "time_reference" -> m.group(9)
    )
  }

  def getRequestMapManifInf(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "stream_type" -> m.group(1),
      "stream_name" -> m.group(2),
      "video_protocol" -> m.group(3),
      "device_type" -> m.group(4),
      "streaming_file_ext" -> m.group(5),
      "inflight_start_time" -> m.group(6),
      "inflight_end_time" -> m.group(7)
    )
  }

  def getRequestMapChunkInf(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "stream_type" -> m.group(1),
      "stream_name" -> m.group(2),
      "video_protocol" -> m.group(3),
      "device_type" -> m.group(4),
      "streaming_file_ext" -> m.group(5),
      "quality" -> m.group(6),
      "fragment_type" -> m.group(7).substring(0,1),
      "time_reference" -> m.group(8)
    )
  }

  def getRequestMapManifVod(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "stream_type" -> m.group(1),
      "mount_point" -> m.group(2),
      "asset_type" -> m.group(3),
      "asset_id" -> m.group(4),
      "device_type" -> m.group(5),
      "streaming_file_ext" -> m.group(6)
    )
  }

  def getRequestMapChunkVod(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "stream_type" -> m.group(1),
      "mount_point" -> m.group(2),
      "asset_type" -> m.group(3),
      "asset_id" -> m.group(4),
      "device_type" -> m.group(5),
      "streaming_file_ext" -> m.group(6),
      "quality" -> m.group(7),
      "fragment_type" -> m.group(8).substring(0,1),
      "time_reference" -> m.group(9)
    )
  }


  // Returns a Map from the CQUUC field
  def requestGetMap( request:String ):Map[String, String] = {
    // Based on the url, we get a regex pattern and immediately
    // parse the "request" field which is the entry for the getMaps functions
    if (request.contains("/live")) {
      if (request.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(infssmanifpathparam)
        val m: Matcher = mainPattern.matcher(request)
        if (m.matches()) {
          getRequestMapManifInf(m)
        } else {
          //println(request)
          //println(infssmanifpathparam)
          Map("RegexError" -> request)
        }
      } else if (request.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(infsschunkpathparam)
        val m: Matcher = mainPattern.matcher(request)
        if (m.matches()) {
          getRequestMapChunkInf(m)
        } else {
          //println(request)
          //println(infsschunkpathparam)
          Map("RegexError" -> request)
        }
      } else {
        Map("inflightMatcherError" -> request)
      }
    } else if (request.contains("/npvr")) {
      if (request.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(npvrssmanifpathparam)
        val m: Matcher = mainPattern.matcher(request)
        if (m.matches()) {
          getRequestMapManifNpvr(m)
        } else {
          //println(request)
          //println(npvrssmanifpathparam)
          Map("RegexError" -> request)
        }
      } else if (request.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(npvrsschunkpathparam)
        val m: Matcher = mainPattern.matcher(request)
        if (m.matches()) {
          getRequestMapChunkNpvr(m)
        } else {
          //println(request)
          //println(npvrsschunkpathparam)
          Map("RegexError" -> request)
        }
      } else {
        Map("npvrMatcherError" -> request)
      }
    } else if (request.contains("/vod")) {
      if (request.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(vodssmanifpathparam)
        val m: Matcher = mainPattern.matcher(request)
        if (m.matches()) {
          getRequestMapManifVod(m)
        } else {
          //println(request)
          //println(vodssmanifpathparam)
          Map("RegexError" -> request)
        }

      } else if (request.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(vodsschunkpathparam)
        val m: Matcher = mainPattern.matcher(request)
        if (m.matches()) {
          getRequestMapChunkVod(m)
        } else {
          //println(request)
          //println(vodsschunkpathparam)
          Map("RegexError" -> request)
        }
      } else {
        Map("vodMatcherError" -> request)
      }
    }
    else {
      Map("none" -> request)
    }
  }
}