package models

import slick.jdbc.H2Profile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}

/**
  * The Post entity.
  *
  * @param id      an id of the post
  * @param title   a title of the post
  * @param content a content of the post
  */
case class Post(id: Option[Long] = None,
                title: String,
                content: String)

/**
  * Defined slick table for entity 'Post'
  */
object Post extends ((Option[Long], String, String) => Post) {

  class Table(slickTag: SlickTag) extends SlickTable[Post](slickTag, "POSTS") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def title = column[String]("TITLE")

    def content = column[String]("CONTENT")

    def * = (id.?, title, content).mapTo[Post]
  }

}