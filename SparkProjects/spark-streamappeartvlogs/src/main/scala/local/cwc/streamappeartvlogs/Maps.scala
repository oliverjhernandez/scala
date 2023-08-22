package local.cwc.streamappeartvlogs

import java.util.regex.Matcher
import local.cwc.streamappeartvlogs.Parser.parseDate

object Maps {

  def getALARM_CLRMainMap(m:Matcher):Map[String,String] = {
    Map[String, String](
      "logdate" -> parseDate(m.group(1)),
    "ip" -> m.group(2),
    "stream" -> m.group(3),
    "alarmtype" -> m.group(4),
    "array" -> m.group(5),
    "message" -> m.group(6)
    )
  }

  def getALARM_SETMainMap(m:Matcher):Map[String,String] = {
    Map[String, String](
      "logdate" -> parseDate(m.group(1)),
      "ip" -> m.group(2),
      "stream" -> m.group(3),
      "alarmtype" -> m.group(4),
      "array" -> m.group(5),
      "message" -> m.group(6),
      "channel" -> m.group(7)
    )
  }

}
