package controllers

import util.ThemeViews
import play.api.mvc.{Result, AnyContent, Action, Controller}
import domain.data.EdblogSqueryl._
import domain.{Tag, Category, Page, Post}
import domain.data.Data
import play.api.Play


object Application extends Controller {
  val postsPerPage: Long = Play.current.configuration.getLong("blog.posts_per_page").getOrElse(10L)
  val notFound = NotFound(ThemeViews.notFound.render())

  def index: Action[AnyContent] = Action {
    Ok("boop")
  }

  def page(slug: String): Action[AnyContent] = Action {
    transaction {
      Data.pages.where(p => p.slug === slug).headOption
    } match {
      case Some(p: Page) => Ok(ThemeViews.page.render(p))
      case None => notFound
    }
  }

  def singlePost(slug: String, page: Long, year: Int = 0, month: Int = 0): Action[AnyContent] = Action {
    transaction {
      Post.getBySlug(slug) match {
        case None => notFound
        case Some(p: Post) => Ok(ThemeViews.post.render(p))
      }
    }
  }
  def allPosts(page: Long): Action[AnyContent] = Action {
    val postCount = Post.postCount

    if (postCount == 0) {
      notFound
    } else {
      inTransaction {
        Ok(ThemeViews.allPosts.render(page, postCount / postsPerPage, None,
          from(Data.posts)(p =>
            select(p)
            orderBy(p.postedOn desc)).page(((page - 1) * postsPerPage).toInt, postsPerPage.toInt).toSeq))
      }
    }
  }
  def postsByCategory(category: String, page: Long): Action[AnyContent] = Action {
    inTransaction {
      Data.categories.where(c => c.slug === category).headOption match {
        case None => notFound
        case Some(cat: Category) => {
          val posts = from(Data.posts)(p =>
            where(p.categoryId === cat.id)
            select(p)
            orderBy(p.postedOn desc)
          ).page(((page - 1) * postsPerPage).toInt, postsPerPage.toInt).toSeq

          Ok(ThemeViews.categoryPosts.render(page, cat.postCount / postsPerPage, cat, posts))
        }
      }

    }
  }
  def postsByTag(tag: String, page: Long): Action[AnyContent] = Action {
    inTransaction {
      Data.tags.where(t => t.slug === tag).headOption match {
        case None => notFound
        case Some(tag: Tag) => {
          val posts = from(Data.postsToTags.right(tag))(p =>
            select(p)
            orderBy(p.postedOn desc)
          ).page(((page - 1) * postsPerPage).toInt, postsPerPage.toInt).toSeq

          Ok(ThemeViews.tagPosts.render(page, tag.postCount / postsPerPage, tag, posts))
        }
      }
    }
  }



  private val themeAssetPath = s"/public/${ThemeViews.themeName}"
  private val sharedAssetPath = "/public/shared"
  def asset(file: String): Action[AnyContent] = {
    controllers.Assets.at(themeAssetPath, file) match {
      case NotFound => {
        controllers.Assets.at(sharedAssetPath, file)
      }
      case a: Action[AnyContent] => a
    }
  }
}