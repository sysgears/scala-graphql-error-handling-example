package validators

import scala.concurrent.Future

/**
  * Determines validation functions for the Post entity.
  */
trait PostValidator {

  /**
    * Validates a title of the post.
    *
    * @param title    a title to be validated
    * @param callback a callback function that will be executed if the title passes validation
    * @tparam T generic return type
    * @return result of the callback function
    */
  def withTitleValidation[T](title: String)(callback: => Future[T]): Future[T]
}