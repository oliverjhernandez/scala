package local.cwc.streamatsvspplog

import java.util.regex.{Matcher, Pattern}

import local.cwc.streamatsvspplog.Regex._
import local.cwc.streamatsvspplog.Parser.parseApacheDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object Maps {

  def getFinalMap(agg:((String, String, String, String),Long)):Map[String, String] = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val date = ZonedDateTime.now().format(formatter)
    Map[String, String](
      "chi" -> agg._1._1,
      "chunks" -> agg._2.toString,
      "country_code" -> country_map(agg._1._1.split('.')(2)),
      "device_type" -> "stb",
      "host" -> agg._1._4,
      "seconds" -> (agg._2 * 3).toString,
      "stream_name" -> agg._1._2,
      "stream_type" -> agg._1._3,
      "timestamp" -> date.toString
    )
  }

  def getMainMap(m:Matcher): Map[String, String] = {

    Map[String, String](
      "chi" -> m.group(1),
      "caun" -> m.group(2),
      "cqtn" -> parseApacheDate(m.group(3)),
      "cqtx" -> m.group(4),
      "pssc" -> m.group(5),
      "pscl" -> m.group(6),
      "sssc" -> m.group(7),
      "sscl" -> m.group(8),
      "cqbl" -> m.group(9),
      "pqbl" -> m.group(10),
      "cqhl" -> m.group(11),
      "pshl" -> m.group(12),
      "pqhl" -> m.group(13),
      "sshl" -> m.group(14),
      "tts" -> m.group(15),
      "phr" -> m.group(16),
      "cfsc" -> m.group(17),
      "pfsc" -> m.group(18),
      "crc" -> m.group(19),
      "cquuc" -> m.group(20),
      "phi" -> m.group(21),
      "shn" -> m.group(22),
      "stms" -> m.group(23),
      "phn" -> m.group(24)
    )
  }

  def getCquucMapManifLive(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(2),
      "stream_name" -> m.group(3),
      "video_protocol" -> m.group(4),
      "device_type" -> m.group(5),
      "streaming_file_ext" -> m.group(6)
    )
  }

  def getCquucMapChunkLive(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(2),
      "stream_name" -> m.group(3),
      "video_protocol" -> m.group(4),
      "device_type" -> m.group(5),
      "streaming_file_ext" -> m.group(6),
      "quality" -> m.group(7),
      "fragment_type" -> m.group(8),
      "time_reference" -> m.group(9)
    )
  }

  def getCquucMapManifNpvr(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(2),
      "mount_point" -> m.group(3),
      "stream_name" -> m.group(4),
      "asset_id" -> m.group(5),
      "device_type" -> m.group(6),
      "streaming_file_ext" -> m.group(7),
      "vbegin" -> m.group(8),
      "vend" -> m.group(9)
    )
  }

  def getCquucMapChunkNpvr(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(2),
      "mount_point" -> m.group(3),
      "stream_name" -> m.group(4),
      "asset_id" -> m.group(5),
      "device_type" -> m.group(6),
      "streaming_file_ext" -> m.group(7),
      "quality" -> m.group(8),
      "fragment_type" -> m.group(9),
      "time_reference" -> m.group(10)
    )
  }

  def getCquucMapManifInf(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(1).split('.')(1),
      "video_protocol" -> m.group(2),
      "stream_name" -> m.group(3),
      "streaming_file_ext" -> m.group(4),
      "vspp_profile" -> m.group(5),
      "inflight_start_time" -> m.group(6),
      "inflight_end_time" -> m.group(7)
    )
  }

  def getCquucMapChunkInf(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(1).split('.')(1),
      "video_protocol" -> m.group(2),
      "stream_name" -> m.group(3),
      "streaming_file_ext" -> m.group(4),
      "session_id" -> m.group(5),
      "quality" -> m.group(6),
      "fragment_type" -> m.group(7),
      "time_reference" -> m.group(8)
    )
  }

  def getCquucMapManifVod(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(2),
      "mount_point" -> m.group(3),
      "asset_type" -> m.group(4),
      "asset_id" -> m.group(5),
      "device_type" -> m.group(6),
      "streaming_file_ext" -> m.group(7)
    )
  }

  def getCquucMapChunkVod(m:Matcher):Map[String, String] = {
    Map[String, String] (
      "domain_request" -> m.group(1),
      "stream_type" -> m.group(2),
      "mount_point" -> m.group(3),
      "asset_type" -> m.group(4),
      "asset_id" -> m.group(5),
      "device_type" -> m.group(6),
      "streaming_file_ext" -> m.group(7),
      "quality" -> m.group(8),
      "fragment_type" -> m.group(9),
      "time_reference" -> m.group(10)
    )
  }


  // Returns a Map from the CQUUC field
  def cquucGetMap( cquuc:String ):Map[String, String] = {
    // Based on the url, we get a regex pattern and immediately
    // parse the "cquuc" field which is the entry for the getMaps functions
    if (cquuc.contains(".live.")) {
      if (cquuc.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(livessmanifuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapManifLive(m)
        } else {
          Map("RegexError" -> cquuc)
        }
      } else if (cquuc.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(livesschunkuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapChunkLive(m)
        } else {
          Map("RegexError" -> cquuc)
        }
      } else {
        Map("liveMathcherError" -> cquuc)
      }
    } else if (cquuc.contains(".inflight.")) {
      if (cquuc.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(infssmanifuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapManifInf(m)
        } else {
          //println(infssmanifuri)
          //println(cquuc)
          Map("RegexError" -> cquuc)
        }
      } else if (cquuc.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(infsschunkuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapChunkInf(m)
        } else {
          println(infsschunkuri)
          println(cquuc)
          Map("RegexError" -> cquuc)
        }
      } else {
        Map("inflightMatcherError" -> cquuc)
      }
    } else if (cquuc.contains(".npvr.")) {
      if (cquuc.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(npvrssmanifuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapManifNpvr(m)
        } else {
          Map("RegexError" -> cquuc)
        }
      } else if (cquuc.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(npvrsschunkuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapChunkNpvr(m)
        } else {
          Map("RegexError" -> cquuc)
        }
      } else {
        Map("npvrMatcherError" -> cquuc)
      }
    } else if (cquuc.contains(".vod.")) {
      if (cquuc.contains("Manifest")) {
        val mainPattern: Pattern = Pattern.compile(vodssmanifuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapManifVod(m)
        } else {
          Map("RegexError" -> cquuc)
        }

      } else if (cquuc.contains("QualityLevels")) {
        val mainPattern: Pattern = Pattern.compile(vodsschunkuri)
        val m: Matcher = mainPattern.matcher(cquuc)
        if (m.matches()) {
          getCquucMapChunkVod(m)
        } else {
          Map("RegexError" -> cquuc)
        }
      } else {
        Map("vodMatcherError" -> cquuc)
      }
    }
    else {
      Map("none" -> cquuc)
    }
  }

  val country_map: Map[String, String]= Map(
    "29" -> "jm",
    "41" -> "tt",
    "169" -> "jm_cl",
    "171" -> "jm_mb",
    "97" -> "bb",
    "109" -> "bs",
    "107" -> "ky",
    "105" -> "tci",
    "111" -> "ai",
    "121" -> "vg",
    "13" -> "an",
    "175" -> "kn"
  )
}
