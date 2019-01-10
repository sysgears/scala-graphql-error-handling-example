package models

case class Post(id: Option[Long] = None,
                title: String,
                content: String)