package com.markusjais.scala.akka.examples.supervision

case class Item(name: String, price:  Double)

// dummy. don't use Long for Ids, use a proper value class in read life
case class BasicOrder(userId: Long, items: List[Item])


case class FinalOrder(userId: Long, items: List[Item], finalPrice: Double)