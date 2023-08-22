package local.cwc.streamatsvspplog

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
  val sessionid = s"\\b(?:\\w|\\!|\\-|\\.)+\\b"

  val infparam = s"\\?$word=($word)\\&$word=($timestamp_iso8601);($timestamp_iso8601)&$word=$word(?:&$word=$scid)?"
  val npvrparam = s"\\?$word=($word)&$word=($word)&$word=$word"

  val livechunk = s"\\/Events\\((?:$word)\\)\\/QualityLevels\\(($word)\\)\\/Fragments\\(($word)=($word)\\)"
  val infchunk = s"\\/QualityLevels\\(($word)\\)\\/Fragments\\(($word)=($word)\\)"
  val vodchunk = s"\\/QualityLevels\\(($word)\\)\\/Fragments\\(($word)=($word)\\)"
  val npvrchunk = s"\\/QualityLevels\\(($word)\\)\\/Fragments\\(($word)=($word)\\)"
  //////////////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////////////
  // Live SS Manifest /////////////////////////////////////////////////////////////////////
  val livessmanifpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/$word\\.($word)\\/$word"
  val livessmanifpathparam = s"$livessmanifpath(?:$param)?"
  val livessmanifuri = s"$uriproto:\\/\\/($urihost)?(?:$livessmanifpathparam)?"

  // Live SS Chunk
  val livesschunkpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/$word\\.($word)"
  val livesschunkpathparam = s"$livesschunkpath$livechunk(?:$param)?"
  val livesschunkuri = s"$uriproto:\\/\\/($urihost)?(?:$livesschunkpathparam)?"

  // Live HLS Manifest
  // Live HLS Chunk
  //////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////////////
  // Inflight SS Manifest /////////////////////////////////////////////////////////////////
  val infssmanifpath = s"\\/($word)\\/$word\\$$($word)\\/$word\\.($word)\\/$word"
  val infssmanifpathparam = s"$infssmanifpath(?:$infparam)?"
  val infssmanifuri = s"$uriproto:\\/\\/($urihost)?(?:$infssmanifpathparam)?"

  // Inflight SS Chunk
  val infsschunkpath = s"\\/($word)\\/$word\\$$($word)\\/$word\\.($word)\\/($sessionid)"
  val infsschunkpathparam = s"$infsschunkpath$infchunk(?:$param)?"
  val infsschunkuri = s"$uriproto:\\/\\/($urihost)?(?:$infsschunkpathparam)?"

  // Inflight HLS Manifest
  // Inflight HLS Chunk
  //////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////////////
  // VOD SS Manifest //////////////////////////////////////////////////////////////////////
  val vodssmanifpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/($word)\\.($word)\\/$word"
  val vodssmanifpathparam = s"$vodssmanifpath(?:$param)?"
  val vodssmanifuri = s"$uriproto:\\/\\/($urihost)?(?:$vodssmanifpathparam)?"

  // VOD SS Chunk
  val vodsschunkpath = s"\\/($word)\\/($word)\\/($word)\\/($word)\\/($word)\\.($word)"
  val vodsschunkpathparam = s"$vodsschunkpath$vodchunk(?:$param)?"
  val vodsschunkuri = s"$uriproto:\\/\\/($urihost)?(?:$vodsschunkpathparam)?"

  // VOD HLS Manifest
  // VOD HLS Chunk
  //////////////////////////////////////////////////////////////////////////////////////


  //////////////////////////////////////////////////////////////////////////////////////
  // NPVR SS Manifest /////////////////////////////////////////////////////////////////////
  val npvrssmanifpath = s"\\/($word)\\/($word)\\/$word\\/($word)\\/((?:$word-*)+)\\/($word)\\.($word)\\/$word"
  val npvrssmanifpathparam = s"$npvrssmanifpath(?:$npvrparam)?"
  val npvrssmanifuri = s"$uriproto:\\/\\/($urihost)?(?:$npvrssmanifpathparam)?"

  // NPVR SS Chunk
  val npvrsschunkpath = s"\\/($word)\\/($word)\\/$word\\/($word)\\/((?:$word-*)+)\\/($word)\\.($word)"
  val npvrsschunkpathparam = s"$npvrsschunkpath$npvrchunk(?:$param)?"
  val npvrsschunkuri = s"$uriproto:\\/\\/($urihost)?(?:$npvrsschunkpathparam)?"

  // NPVR HLS Manifest
  // NPVR HLS Chunk
  //////////////////////////////////////////////////////////////////////////////////////

}
