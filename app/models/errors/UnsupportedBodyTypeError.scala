package models.errors

/**
  * Represents an exception object indicating that request body has unsupported type.
  *
  * @param msg an exception message to show
  */
case class UnsupportedBodyTypeError(msg: String) extends Exception(msg)