package models.errors

/**
  * Represents an exception object indicating that GraphQL query is too complex.
  *
  * @param msg an exception message to show
  */
case class TooComplexQueryError(msg: String = "Query is too expensive.") extends Exception(msg)