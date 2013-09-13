package com.markusjais.scala.akka.examples.supervision

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import akka.actor.SupervisorStrategy.{ Stop, Resume, Restart }
import scala.concurrent.duration._
import scala.util.Random
import akka.actor.OneForOneStrategy

class MySqlBrokenConnectionException(message: String)
  extends Exception(message)

class MysqlConnection() {
  def saveOrder(order: FinalOrder) = {
    println(s"writing $order to MySQL")
    //  fake broken DB connection
    if (Random.nextInt(15) % 5 == 0) {
      throw new MySqlBrokenConnectionException("MySQL Connection Error")
    }
  }
}

class OrderWriterActor extends Actor {
  val log = Logging(context.system, OrderWriterActor.this)

  import OrderWriterActor._

  val system = ActorSystem("mySystem")

  var mySqlConnection: MysqlConnection = _

  override def preStart = {
    // dummy. would need ip, port, etc 		
    println("_________________________________________________________restaring OrderWriter")
    mySqlConnection = new MysqlConnection
  }

  def receive = {

    case SaveOrder(order) => {

      mySqlConnection.saveOrder(order)

    }
    case _ => log.error("wrong message")
  }
}

object OrderWriterActor {
  case class SaveOrder(order: FinalOrder)
}

class WriterSupervisor(writerProps: Props) extends Actor {
  override def supervisorStrategy = OneForOneStrategy() {
    case ex: MySqlBrokenConnectionException => Restart
    // case ex: BrokenDiskException => Stop
  }
  val writer = context.actorOf(writerProps)
  def receive = {
    case m => writer forward (m)
  }
}




















