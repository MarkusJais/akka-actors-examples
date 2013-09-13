package com.markusjais.scala.akka.examples.supervision

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.{ Stop, Resume, Restart }
import akka.actor.ActorRef
import com.markusjais.scala.akka.examples.supervision.OrderWriterActor.SaveOrder

class ProcessorActor(writerSupervisor: ActorRef) extends Actor {
  val log = Logging(context.system, ProcessorActor.this)

  def receive = {
    case order: BasicOrder => { 
      log.info(s"got order: $order")
      val finalOrder = new FinalOrder(order.userId, order.items, calculatePrice(order.items))
      writerSupervisor ! new SaveOrder(finalOrder)
    }
    case _ => log.error("wrong message")
  }
  
  // dummy method: sum up prices and add 20% VAT
  def calculatePrice(items: List[Item]) = {
    items.map(x => x.price).reduceLeft(_ + _) * 1.2
  }
  
  override def preStart = {
    // dummy. would need ip, port, etc 		
    println("__________xxxx_______________________________________________restaring Processor")
    
  }
  
}

class InvalidOrderException(message: String)
  extends Exception(message)

class ProcessorSupervisor(writerSupervisorProps: Props)
  extends Actor {
  override def supervisorStrategy = OneForOneStrategy() {
    // when order is invalid we ignore it and resume processing the next one
    case _: InvalidOrderException => Resume 
  }
  val writerSupervisor = context.actorOf(writerSupervisorProps) 
  val processorProps = Props(new ProcessorActor(writerSupervisor))
  val processorActor = context.actorOf(processorProps) 

  def receive = {
    case m => processorActor forward (m) 
  }
}
