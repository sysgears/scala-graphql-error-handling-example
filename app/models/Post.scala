package models

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