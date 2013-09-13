package com.markusjais.scala.akka.examples.supervision

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.util.Random
import akka.actor.ActorRef
import akka.actor.AllForOneStrategy
import akka.actor.SupervisorStrategy.{ Stop, Resume, Restart }
import akka.actor.Terminated
import akka.actor.PoisonPill

class CassandraConnection() {

  // TODO make orders random
  def readOrdersFromCassandra(): List[BasicOrder] = {
    
    
    // fake some errors
    val randomInt = Random.nextInt(15)
    if (randomInt % 9 == 0) {
      throw new CassandraBrokenConnectionException("C* Connection Error")
    }
//    if (randomInt % 3 == 0) {
//      throw new BrokenDiskException("Disk failure")
//    }
    
    println("reading")
    // dummy. fake reading from C*
    val jcip = new Item("Java Concurrency in Practice", 12.0)
    val effJava = new Item("Effective Java", 24.0)
    val progRuby = new Item("Programming Ruby", 25.0)
    val railsTutorial = new Item("The Rails  Tutorial", 22.0)

    val javaBookOrder = new BasicOrder(42, List(jcip, effJava))
    val rubyBookOrder = new BasicOrder(99, List(progRuby, railsTutorial))

    List(javaBookOrder, rubyBookOrder)
  }
}

class OrderReaderActor(processorSupervisor: ActorRef) extends Actor {
  val log = Logging(context.system, OrderReaderActor.this)

  import OrderReaderActor._

  val system = ActorSystem("mySystem")
  // val processorActor = system.actorOf(Props[ProcessorActor])

  var cassandraConnection: CassandraConnection = _

  implicit val ec = context.dispatcher
  val ticker = context.system.scheduler.schedule(1.second, 2.second, self, ReadOrders)

  override def preStart = {
    // dummy. would need ip, port, etc 		
    println("restarting OrderReaderActor")
    cassandraConnection = new CassandraConnection
  }
  
  override def postStop = {
    println("stopping OrderReaderActor")
  }

  def receive = {

    case ReadOrders => {
      cassandraConnection.readOrdersFromCassandra.map(processorSupervisor ! _)
    }
    case _ => log.error("wrong message")
  }
  
  

}

object OrderReaderActor {
  case class ReadOrders
}

class CassandraBrokenConnectionException(message: String)
  extends Exception(message)

class BrokenDiskException(message: String)
  extends Exception(message)

class ReaderSupervisor(processorSupervisorProps: Props)
  extends Actor {

  val processorSupervisor = context.actorOf(processorSupervisorProps)
  val readerProps = Props(new OrderReaderActor(processorSupervisor))
  val readerActor = context.actorOf(readerProps)
  
  context.watch(readerActor)
  
  override def supervisorStrategy = AllForOneStrategy() {
    case _: BrokenDiskException => Stop 
  }

  def receive = {
    case Terminated(readerActor) => {
      println("readerActor dead. I kill myself")
      self ! PoisonPill 
    }
  }
}




