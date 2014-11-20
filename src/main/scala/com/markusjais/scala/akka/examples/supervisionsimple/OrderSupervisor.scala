package com.markusjais.scala.akka.examples.supervisionsimple

import akka.actor._
import akka.actor.SupervisorStrategy.{Escalate, Stop, Resume, Restart}
import akka.actor.OneForOneStrategy

class OrderSupervisor extends Actor with ActorLogging {

  override def supervisorStrategy = OneForOneStrategy() {
    case _: DatabaseBrokenConnectionException => Restart
    case _: InvalidOrderException => Resume
    case _: NullPointerException => Escalate
  }

  override def receive = {
    case "init" =>
      val serviceProps = Props(new OrderServiceActor)
      val serviceActor = context.actorOf(serviceProps)
      val clientProps = Props(new OrderClientActor(serviceActor))
      val clientActor = context.actorOf(clientProps) // create client actor to start scheduler inside it
      context.become(initialized, discardOld = true)
  }

  def initialized: Receive = {
    case "ping" => sender ! "still alive"
  }
}
