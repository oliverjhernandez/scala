package local.cwc.streamusplogs

object Regex {

  //////////////////////////////////////////////////////////////////////////////////////
  // Expressions ///////////////////////////////////////////////////////////////////////
  val ipv4 = "(?<![0-9])(?:(?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5]))(?![0-9])"
  val monthday = "(?:(?:0[1-9])|(?:[12][0-9])|(?:3[01])|[1-9])"
  val month = "(?:[Jj]an(?:uary|uar)?|[Ff]eb(?:ruary|ruar)?|[Mm](?:a|ä)?r(?:ch|z)?|[Aa]pr(?:il)?|[Mm]a(?:y|i)?|[Jj]un(?:e|i)?|[Jj]ul(?:y)?|[Aa]ug(?:ust)?|[Ss]ep(?:tember)?|[Oo](?:c|k)?t(?:ober)?|[Nn]ov(?:ember)?|[Dd]e(?:c|z)(?:ember)?)"
  val monthnum = "(?:0?[1-9]|1[0-2])"
  val year = "(?>\\d\\d){1,2}"
  val hour = "(?:2[0123]|[01]?[0-9])"
  val minute = "(?:[0-5][0-9])"
  val second = "(?:(?:[0-5]?[0-9]|60)(?:[:.,][0-9]+)?)"
  val number = s"(?:(?<![0-9.+-])(?>[+-]?(?:(?:[0-9]+(?:\\.[0-9]+)?)|(?:\\.[0-9]+))))"
  val int = "(?:[+-]?(?:[0-9]+))"
  val word = "\\b\\w+\\b"
  val uriproto = "[A-Za-z0-9+\\-.]+"
  val user = "[a-zA-Z0-9._-]+"
  val useragent = s".+?[\\/\\ç\\s][\\d.]+"
  val anything = "[^\"]*"
  val hostname = "\\b(?:[0-9A-Za-z][0-9A-Za-z-]{0,62})(?:\\.(?:[0-9A-Za-z][0-9A-Za-z-]{0,62}))*(?:\\.?|\\b)"
  val posint = "\\b(?:[1-9][0-9]*)\\b"
  val time = s"(?!<[0-9])$hour:$minute(?::$second)(?![0-9])"
  val httpdate = s"$monthday\\/$month\\/$year:$time $int"
  val iporhost = s"$ipv4|$hostname"
  val urihost = s"$iporhost(?::$posint(?:$int)?})?"
  val param = "\\?[A-Za-z0-9$.+!*'|(){},~@#%&\\:\\/=:;_?\\-\\[\\]<>]*"
  val iso8601_tz = s"(?:Z|[+-]$hour(?::?$minute}))"
  val timestamp_iso8601 = s"$year-$monthnum-$monthday[T ]$hour:?$minute(?::?$second)?(?:$iso8601_tz)?"
  val uripath = "(?:\\/[A-Za-z0-9$.+!*'(){},~:;=@#%_\\-]*)+"
  val uriparam = "\\?[A-Za-z0-9$.+!*'|(){},~@#%&\\/=:;_?\\-\\[\\]]*"
  val uripathparam = s"$uripath(?:$uriparam)?"
  val uri = s"$uriproto:\\/\\/(?:$user(?::[^@]*)?@)?(?:$urihost)?(?:$uripathparam)?"
  val scid = s"scid!$word"


  val infparam = s"\\?$word=($timestamp_iso8601);($timestamp_iso8601)&$word=$word&$word=$word"
  val npvrparam = s"\\?$word=($word)&$word=($word)&$word=$word&$word=$word"

  val livechunk = s"\\/$word\\((?:$word)\\)\\/$word\\(($word)\\)\\/$word\\(($word)=($word)\\)"
  val vodchunk = s"\\/$word\\(($word)\\)\\/$word\\(($word)=($word)\\)"
  val npvrchunk = s"\\/$word\\(($word)\\)\\/$word\\(($word)=($word)\\)"
  //////////////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////////////
  // VOD SS Manifest //////////////////////////////////////////////////////////////////////
  val vodssmanifpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/($word)\\.($word)\\/$word"
  val vodssmanifpathparam = s"$vodssmanifpath(?:$param)?"

  // VOD SS Chunk
  val vodsschunkpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/($word)\\.($word)"
  val vodsschunkpathparam = s"$vodsschunkpath$vodchunk(?:$param)?"
  //////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////////////
  // Inflight SS Manifest /////////////////////////////////////////////////////////////////
  val infssmanifpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/$word\\.($word)\\/$word"
  val infssmanifpathparam = s"$infssmanifpath(?:$infparam)?"

  // Inflight SS Chunk
  val infsschunkpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/$word\\.($word)"
  val infsschunkpathparam = s"$infsschunkpath$livechunk(?:$param)?"
  //////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////////////
  // NPVR SS Manifest /////////////////////////////////////////////////////////////////////
  val npvrssmanifpath = s"\\/($word)\\/($word)\\/$word\\/($word)\\/((?:$word-*)+)\\/($word)\\.($word)\\/$word"
  val npvrssmanifpathparam = s"$npvrssmanifpath(?:$npvrparam)?"

  // NPVR SS Chunk
  val npvrsschunkpath = s"\\/($word)\\/($word)\\/$word\\/($word)\\/((?:$word-*)+)\\/($word)\\.($word)"
  val npvrsschunkpathparam = s"$npvrsschunkpath$npvrchunk(?:$param)?"
  //////////////////////////////////////////////////////////////////////////////////////

}