package util

import play.api.Play

/**
 * Created by ed on 7/17/14.
 */
object BlogSettings {
  val title = Play.current.configuration.getString("blog.title").getOrElse("No Title Given")
  val subtitle = Play.current.configuration.getString("blog.subtitle").getOrElse("No Subtitle Given")
  val author = Play.current.configuration.getString("blog.author").getOrElse("Anonymous")
  val email = Play.current.configuration.getString("blog.email").getOrElse("mailto:anonymous@example.com")
}
