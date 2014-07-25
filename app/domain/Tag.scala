package domain

import org.squeryl.KeyedEntity
import domain.data.EdblogSqueryl._
import domain.data.Data
import scala.concurrent.duration.Duration
import play.api.Play
import java.util.concurrent.TimeUnit
import play.api.cache.Cache
import play.api.Play.current

/**
 * Created by ed on 7/14/14.
 */
case class Tag(slug: String, name: String, description: String) extends KeyedEntity[Long] {
  val id: Long = 0

  def reverseRoute(page: Long = 1) = controllers.routes.Application.postsByTag(slug, page)

  def postCount = inTransaction {
    from(Data.postsToTags.right(this))(p =>
      compute(count)
    )
  }

  def cache() = {
    Cache.set(s"tags.slug.${slug}", this, Tag.cacheTime)
    Cache.set(s"tags.id.${id}", this, Tag.cacheTime)
  }
}

object Tag {
  private val cacheTime = Duration.apply(Play.current.configuration.getMilliseconds("blog.tags.cache_time").getOrElse(0L), TimeUnit.MILLISECONDS)

  def fromCache(id: Long): Option[Tag] = Cache.get(s"tags.id.${id}").map(o => o.asInstanceOf[Tag])
  def fromCache(slug: String): Option[Tag] = Cache.get(s"tags.slug.${slug}").map(o => o.asInstanceOf[Tag])
  def getById(id: Long): Option[Tag] = inTransaction {
    fromCache(id) match {
      case s: Some[Tag] => s
      case None => {
        val tag = Data.tags.where(p => p.id === id).headOption
        tag.foreach(c => c.cache())
        tag
      }
    }
  }
  def getBySlug(slug: String): Option[Tag] = inTransaction {
    fromCache(slug) match {
      case s: Some[Tag] => s
      case None => {
        val tag = Data.tags.where(p => p.slug === slug).headOption
        tag.foreach(c => c.cache())
        tag
      }
    }
  }


  def tagCloud(): Seq[(Tag, Int)] = {
    Cache.get("tags.weights").map(o => o.toString) match {
      case Some(s: String) => {
        s.split(";").map(pair => pair.split("=")).map(tokens => (getBySlug(tokens(0)).get, tokens(1).toInt)).toSeq
      }
      case None => inTransaction {
        val counts = from(Data.postsToTags)( pt =>
          groupBy(pt.tagId)
          compute(countDistinct(pt.postId))
        ).map(t => (getById(t.key).get, t.measures)).toSeq

        val maxWeight = Math.max(1, counts.map(t => t._2).reduceLeft((x, y) => if (x > y) x else y))
        val weights = counts.map(t => (t._1, ((t._2 / maxWeight.toDouble) * 10).ceil.toInt))
        val shuffled = weights.sortBy(_._1.hashCode())

        Cache.set("tags.weights", shuffled.map(w => s"${w._1.slug}=${w._2}").mkString(";"), cacheTime)

        shuffled
      }
    }
  }
}