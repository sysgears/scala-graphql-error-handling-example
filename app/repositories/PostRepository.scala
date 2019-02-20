package repositories

import models.Post

import scala.concurrent.Future

/**
  * A repository trait that determines basic CRUD operations on the Post entity.
  */
trait PostRepository {

  /**
    * Creates a post.
    *
    * @param post a new post
    * @return created post
    */
  def create(post: Post): Future[Post]

  /**
    * Returns a post id.
    *
    * @param id an id of the post
    * @return found post
    */
  def find(id: Long): Future[Option[Post]]

  /**
    * Returns a list of posts.
    *
    * @return list of posts
    */
  def findAll(): Future[List[Post]]

  /**
    * Updates an existing post.
    *
    * @param post a post to be updated
    * @return updated post
    */
  def update(post: Post): Future[Post]

  /**
    * Deletes an existing post by id.
    *
    * @param id an id of the post
    * @return true if the post was deleted, else otherwise
    */
  def delete(id: Long): Future[Boolean]
}
