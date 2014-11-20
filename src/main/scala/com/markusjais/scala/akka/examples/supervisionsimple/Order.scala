package com.markusjais.scala.akka.examples.supervisionsimple

case class Book(title: String, price:  Double)

// dummy. don't use Long for Ids, use a proper value class in read life
case class BookOrder(userId: Long, items: List[Book])

