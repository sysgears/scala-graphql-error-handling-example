package validators

import models.errors.InvalidTitle

import scala.concurrent.Future

/**
  * Provides validation functions for the Post entity.
  */
class PostValidatorImpl extends PostValidator {

  /**
    * A regex that defines what the title should be.
    */
  val titleRegex = "[a-zA-Z0-9- ]{3,100}"

  /** @inheritdoc*/
  override def withTitleValidation[T](title: String)(callback: => Future[T]): Future[T] = {
    if (title.matches(titleRegex)) callback else Future.failed(InvalidTitle("Post's title is invalid."))
  }
}