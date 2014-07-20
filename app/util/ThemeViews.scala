package util

import play.api.Play
import play.api.templates._
import domain.{Tag, Category, Page, Post}
import scala.util.{Failure, Success, Try}
import scala.util.Success
import scala.util.Failure

class ThemeException(message: String, throwable: Throwable = null) extends Exception(message, throwable)

/**
 * Created by ed on 7/17/14.
 */
object ThemeViews {
  val themeName = Play.current.configuration.getString("blog.theme").getOrElse {
    throw new ThemeException("blog.theme must be set.")
  }

  type SinglePostThemeTemplate = Template1[Post, HtmlFormat.Appendable]
  val post: SinglePostThemeTemplate = getTemplate("post")

  type PageThemeTemplate = Template1[Page, HtmlFormat.Appendable]
  val page: PageThemeTemplate = getTemplate("page")

  type AllPostsListThemeTemplate = Template4[Long, Long, Option[String], Seq[Post], HtmlFormat.Appendable]
  val allPosts: AllPostsListThemeTemplate = getTemplate("all_posts")

  type CategoryPostsListThemeTemplate = Template4[Long, Long, Category, Seq[Post], HtmlFormat.Appendable]
  val categoryPosts: CategoryPostsListThemeTemplate = getTemplate("category_posts")

  type TagPostsListThemeTemplate = Template4[Long, Long, Tag, Seq[Post], HtmlFormat.Appendable]
  val tagPosts: TagPostsListThemeTemplate = getTemplate("tag_posts")

  type NotFoundThemeTemplate = Template0[HtmlFormat.Appendable]
  val notFound: NotFoundThemeTemplate = getTemplate("not_found")

  type ErrorThemeTemplate = Template0[HtmlFormat.Appendable]
  val error: ErrorThemeTemplate = getTemplate("error")

  private def getTemplate[TTemplateType](name: String): TTemplateType = {
    val className = s"views.html.${themeName}.${name}"
    Try {
      val cls = Class.forName(className + "$")
      cls.getField("MODULE$").get(cls).asInstanceOf[TTemplateType]
    } match {
      case Success(template) => template
      case Failure(ex) => {
        throw new ThemeException(s"Could not instantiate '${className}'.", ex)
      }
    }
  }
}
