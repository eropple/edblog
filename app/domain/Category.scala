package domain

import org.squeryl.KeyedEntity
import domain.data.Data
import domain.data.EdblogSqueryl._
import play.api.cache.Cache
import scala.concurrent.duration.Duration
import play.api.Play
import java.util.concurrent.TimeUnit
import play.api.Play.current

/**
 * Created by ed on 7/14/14.
 */
case class Category(slug: String, name: String, description: String) extends KeyedEntity[Long] {
  val id: Long = 0

  def reverseRoute(page: Long = 1) = controllers.routes.Application.postsByCategory(slug, page)

  def postCount = inTransaction { from(Data.posts)(p => where(p.categoryId === id) compute(count)) }
  def cache() = {
    Cache.set(s"categories.slug.${slug}", this, Category.cacheTime)
    Cache.set(s"categories.id.${id}", this, Category.cacheTime)
  }
}

object Category {
  def countsByCategory(): Seq[(Category, Long)] = {
    Cache.get("categories.counts").map(o => o.toString) match {
      case Some(s: String) => {
        s.split(";").map(pair => pair.split("=")).map(tokens => (getBySlug(tokens(0)).get, tokens(1).toLong)).toSeq
      }
      case None => inTransaction {
        val countQuery = (from(Data.categories, Data.posts)( (c, p) =>
          where (c.id === p.categoryId)
          groupBy(c.id)
          compute(countDistinct(p.id))
        ) map( c => {
          (getById(c.key).get, c.measures)
        })).toSeq.sortBy(c => c._2).reverse.toSeq

        Cache.set("categories.counts", countQuery.map(c => s"${c._1.slug}=${c._2}").mkString(";"), cacheTime)

        countQuery
      }
    }
  }



  private val cacheTime = Duration.apply(Play.current.configuration.getMilliseconds("blog.categories.cache_time").getOrElse(0L), TimeUnit.MILLISECONDS)

  def fromCache(id: Long): Option[Category] = Cache.get(s"categories.id.${id}").map(o => o.asInstanceOf[Category])
  def fromCache(slug: String): Option[Category] = Cache.get(s"categories.slug.${slug}").map(o => o.asInstanceOf[Category])
  def getById(id: Long): Option[Category] = inTransaction {
    fromCache(id) match {
      case s: Some[Category] => s
      case None => {
        val cat = Data.categories.where(p => p.id === id).headOption
        cat.foreach(c => c.cache())
        cat
      }
    }
  }
  def getBySlug(slug: String): Option[Category] = inTransaction {
    fromCache(slug) match {
      case s: Some[Category] => s
      case None => {
        val cat = Data.categories.where(p => p.slug === slug).headOption
        cat.foreach(c => c.cache())
        cat
      }
    }
  }
}