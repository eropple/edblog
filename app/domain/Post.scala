package domain

import org.joda.time.DateTime
import domain.data.EdblogSqueryl._
import org.squeryl.KeyedEntity
import org.squeryl.dsl.{OneToMany, ManyToMany, CompositeKey2}
import domain.data.Data
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import util.DateTimeHelper
import scala.concurrent.duration.Duration
import play.api.Play
import java.util.concurrent.TimeUnit
import play.api.cache.Cache
import play.api.Play.current

/**
 * Created by ed on 7/14/14.
 */
class Post(val slug: String,
           title: String, body: String,
           val postedOn: DateTime, val categoryId: Long)
        extends FormattableContent(title, body) with KeyedEntity[Long] {

  import DateTimeHelper._

  val id: Long = 0
  lazy val category: Category = inTransaction {
    Data.categories.where(c => c.id === categoryId).head
  }
  lazy val tags: Seq[Tag] = inTransaction { Data.postsToTags.leftStateful(this).toSeq }
  lazy val comments: Seq[Comment] = inTransaction { Data.postToComments.leftStateful(this).toSeq }

  def dateline: String = datelineFormatter.print(postedOn.withZone(displayTimeZone))
  def pubdate: String = pubdateFormatter.print(postedOn.withZone(displayTimeZone))

  def disqusId = s"post-${id}"

  def cache() = {
    Cache.set(s"posts.id.${id}", this, Post.cacheTime)
    Cache.set(s"posts.slug.${slug}", this, Post.cacheTime)
  }

  def reverseRoute(page: Long = 1) = controllers.routes.Application.singlePost(slug, page, postedOn.getYear, postedOn.getMonthOfYear)
}

object Post {
  private val cacheTime = Duration.apply(Play.current.configuration.getMilliseconds("blog.posts.cache_time").getOrElse(0L), TimeUnit.MILLISECONDS)

  def fromCache(id: Long): Option[Post] = Cache.get(s"posts.id.${id}").map(o => o.asInstanceOf[Post])
  def fromCache(slug: String): Option[Post] = Cache.get(s"posts.slug.${slug}").map(o => o.asInstanceOf[Post])

  def postCount: Long = {
    inTransaction {
      from(Data.posts)(p => compute(count))
    }
  }

  def getById(id: Long): Option[Post] = inTransaction {
    val post = Data.posts.where(p => p.id === id).headOption
    if (post.isDefined) post.get.cache()
    post
  }
  def getBySlug(slug: String): Option[Post] = inTransaction {
    val post = Data.posts.where(p => p.slug === slug).headOption
    if (post.isDefined) post.get.cache()
    post
  }
}

case class PostTag(postId: Long, tagId: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(postId, tagId)
}


