package models.errors

case class InvalidTitle(msg: String) extends Exception(msg)