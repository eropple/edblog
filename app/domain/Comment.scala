package domain

import org.joda.time.DateTime
import domain.data.EdblogSqueryl._
import org.squeryl.KeyedEntity
import domain.data.Data

/**
 * Created by ed on 7/14/14.
 */
case class Comment(postId: Long, parentCommentId: Option[Long],
              name: String, email: String,
              website: Option[String], body: String, postedOn: DateTime) extends KeyedEntity[Long] {
  val id: Long = 0

  def this() = this(0, Some(0), "", "", Some(""), "", DateTime.now())


  def parentComment: Option[Comment] = Data.comments.where(c => c.id === parentCommentId).headOption
}
class CommentTreeNode(postId: Long, comment: Comment, children: Seq[Comment])