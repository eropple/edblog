package domain

import org.joda.time.DateTime
import anorm.SqlParser._
import org.squeryl.KeyedEntity
import play.api.cache.Cache
import scala.concurrent.duration.Duration
import play.api.Play
import java.util.concurrent.TimeUnit
import play.api.Play.current

/**
 * Created by ed on 7/17/14.
 */
class Page(val slug: String, title: String, body: String)
          extends FormattableContent(title, body) with KeyedEntity[Long] {
  val id: Long = 0

  def cache() = {
    Cache.set(s"pages.id.${id}", this, Page.cacheTime)
    Cache.set(s"pages.slug.${slug}", this, Page.cacheTime)
  }
}

object Page {
  private val cacheTime = Duration.apply(Play.current.configuration.getMilliseconds("blog.pages.cache_time").getOrElse(0L), TimeUnit.MILLISECONDS)

  def fromCache(id: Long): Option[Page] = Cache.get(s"pages.id.${id}").map(o => o.asInstanceOf[Page])
  def fromCache(slug: String): Option[Page] = Cache.get(s"pages.slug.${slug}").map(o => o.asInstanceOf[Page])
}