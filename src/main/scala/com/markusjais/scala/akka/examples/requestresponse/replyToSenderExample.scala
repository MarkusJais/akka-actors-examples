package com.markusjais.scala.akka.examples.requestresponse

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.util.Random

class EchoActor extends Actor {
  val log = Logging(context.system, EchoActor.this)
  def receive = {
    case message: String => sender ! message.toUpperCase()
    case _ => log.warning("not a string")
  }
}

class SenderActor(messages: List[String]) extends Actor {
  val log = Logging(context.system, SenderActor.this)

  val system = ActorSystem("mySystem")
  val echoActor = system.actorOf(Props[EchoActor])

  val runnable = new Runnable {
    override def run = {
      echoActor ! Random.shuffle(messages).head
    }
  }

  override def preStart = {
    context.system.scheduler.schedule(0 seconds, 2 seconds, runnable)(context.dispatcher) //, self)
  }

  def receive = {
    case echoMessage: String => log.info(s"got uppercase echo: $echoMessage")
    case _ => log.warning("not a string")
  }

}

object replyToSenderExample extends App {

  // actors live inside an Actor System
  val system = ActorSystem("mySystem")
  val languages = List("Scala", "Clojure", "Ruby", "Java", "Python")

  val senderActor = system.actorOf(Props(classOf[SenderActor], languages))

  // an Actor System needs to be shut down
  Console.readLine(s"Hit return to stop")
  println("I am done, goodbye")
  system.shutdown()

}
