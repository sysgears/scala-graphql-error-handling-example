package models.errors

case object TooComplexQueryError extends Exception("Query is too expensive.")