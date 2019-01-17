package models.errors

/**
  * Represents an exception object indicating that a title of the Post entity is invalid.
  *
  * @param msg an exception message to show
  */
case class InvalidTitle(msg: String) extends Exception(msg)