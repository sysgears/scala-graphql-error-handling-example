package models.errors

import sangria.execution.UserFacingError

case class NotFound(msg: String) extends Exception(msg) with UserFacingError {
  override def getMessage(): String = msg
}