package repositories

import scala.concurrent.Future

/**
  * A repository trait that determines basic CRUD operations.
  *
  * @tparam T type of entity on which operations are performed
  */
trait Repository[T] {

  /**
    * Creates an instance.
    *
    * @param item a new instance
    * @return created instance
    */
  def create(item: T): Future[T]

  /**
    * Returns an instance by id.
    *
    * @param id an id of the instance
    * @return found instance
    */
  def find(id: Long): Future[Option[T]]

  /**
    * Returns a list of instances.
    *
    * @return list of instance
    */
  def findAll(): Future[List[T]]

  /**
    * Updates an existing instance.
    *
    * @param item new instance
    * @return updated instance
    */
  def update(item: T): Future[T]

  /**
    * Delete an existing instance by id.
    *
    * @param id an id of some instance
    * @return true/false result of deleting
    */
  def delete(id: Long): Future[Boolean]
}
