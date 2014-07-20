package util

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTimeZone
import play.api.Play

/**
 * Created by ed on 7/19/14.
 */
object DateTimeHelper {
  val displayTimeZone = DateTimeZone.forID(Play.current.configuration.getString("blog.time_zone").getOrElse("Etc/UTC"))

  val datelineFormatter = DateTimeFormat.forPattern("MMMM d, YYYY '&mdash;' K:mm a z")
  val pubdateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")
}
