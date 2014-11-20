package com.markusjais.scala.akka.examples.supervisionsimple

import akka.actor.{ActorLogging, Actor}
import scala.util.Random

class DatabaseBrokenConnectionException(message: String)
  extends Exception(message)

class InvalidOrderException(message: String)
  extends Exception(message)

class DatabaseConnection() {
  def saveOrder(order: BookOrder) = {
    println(s"writing $order to DB")
    //  fake broken DB connection
    if (Random.nextInt(15) % 2 == 0) {
      println("ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
      throw new DatabaseBrokenConnectionException("DB Connection Error")
    }
    if (Random.nextInt(45) % 45 == 0) {
      throw new NullPointerException
    }
  }
}

class OrderServiceActor extends Actor with ActorLogging {

  var dbConnection: DatabaseConnection = _

  override def preStart = {
    // dummy. would need ip, port, etc 		
    log.info("**************************************************: restarting OrderServiceActor with new DB Connection")
    dbConnection = new DatabaseConnection
  }

  def receive = {
    case order: BookOrder => {
      dbConnection.saveOrder(order)
    }
    case _ => log.error("wrong message")
  }
}




















