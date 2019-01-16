package models.errors

case class TooComplexQueryError(msg: String = "Query is too expensive.") extends Exception(msg)