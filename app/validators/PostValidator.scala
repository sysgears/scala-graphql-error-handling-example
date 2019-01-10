package validators

import scala.concurrent.Future

trait PostValidator {

  def withTitleValidation[T](title: String)(callback: => Future[T]): Future[T]
}
