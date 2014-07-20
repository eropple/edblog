package domain.data

import domain.data.EdblogSqueryl._
import org.squeryl.Schema
import domain._
import domain.Tag

/**
 * Created by ed on 7/19/14.
 */
object Data extends Schema {
  val categories = table[Category]("Categories")
  on(categories)(t => declare(
    t.slug              is(unique, indexed, dbType("CHAR(50)")),
    t.name              is(dbType("TEXT")),
    t.description       is(dbType("TEXT"))
  ))

  val tags = table[Tag]("Tags")
  on(tags)(t => declare(
    t.slug              is(unique, indexed, dbType("CHAR(50)")),
    t.name              is(dbType("TEXT")),
    t.description       is(dbType("TEXT"))
  ))

  val pages = table[Page]("Pages")
  on(pages)(t => declare(
    t.slug              is(unique, indexed, dbType("CHAR(50)")),
    t.title             is(dbType("TEXT")),
    t.body              is(dbType("TEXT"))
  ))

  val posts = table[Post]("Posts")
  on(posts)(t => declare(
    t.slug              is(unique, indexed, dbType("CHAR(50)")),
    t.categoryId        is(indexed),
    t.title             is(dbType("TEXT")),
    t.body              is(dbType("TEXT"))
  ))

  val comments = table[Comment]("Comments")
  on(comments)(t => declare(
    t.postId            is(indexed),
    t.body              is(dbType("TEXT"))
  ))

  val postsToTags = manyToManyRelation(posts, tags, "PostTags")
                      .via[PostTag]((p, t, pt) => (p.id === pt.postId, pt.tagId === t.id))

  val categoryToPosts = oneToManyRelation(categories, posts)
                          .via((c, p) => c.id === p.categoryId)

  val postToComments = oneToManyRelation(posts, comments)
                          .via((p, c) => p.id === c.postId)
}
