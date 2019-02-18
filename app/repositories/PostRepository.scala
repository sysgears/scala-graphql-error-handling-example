package repositories

import models.Post

import scala.concurrent.Future

/**
  * A repository trait that determines basic CRUD operations.
  *
  * @tparam T type of entity on which operations are performed
  */
trait PostRepository {

  /**
    * Creates an instance.
    *
    * @param item a new instance
    * @return created instance
    */
  def create(item: Post): Future[Post]

  /**
    * Returns an instance by id.
    *
    * @param id an id of the instance
    * @return found instance
    */
  def find(id: Long): Future[Option[Post]]

  /**
    * Returns a list of instances.
    *
    * @return list of instance
    */
  def findAll(): Future[List[Post]]

  /**
    * Updates an existing instance.
    *
    * @param item new instance
    * @return updated instance
    */
  def update(item: Post): Future[Post]

  /**
    * Delete an existing instance by id.
    *
    * @param id an id of some instance
    * @return true/false result of deleting
    */
  def delete(id: Long): Future[Boolean]
}
