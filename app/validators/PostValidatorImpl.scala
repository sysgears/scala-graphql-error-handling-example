package validators

import models.errors.InvalidTitle

import scala.concurrent.Future

class PostValidatorImpl extends PostValidator {

  val titleRegex = "[a-zA-Z0-9- ]{3,100}"

  /** @inheritdoc*/
  override def withTitleValidation[T](title: String)(callback: => Future[T]): Future[T] = {
    if (title.matches(titleRegex)) callback else Future.failed(InvalidTitle("Post's title is invalid."))
  }
}