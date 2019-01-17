package models.errors

import sangria.execution.UserFacingError

case class AlreadyExists(msg: String) extends Exception with UserFacingError {
  override def getMessage: String = msg
}