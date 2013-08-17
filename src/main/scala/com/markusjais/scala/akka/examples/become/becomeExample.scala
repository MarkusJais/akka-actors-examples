package com.markusjais.scala.akka.examples.become

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.util.Success
import scala.util.Failure

class LoadMonitorActor extends Actor {
  import context._
  val log = Logging(context.system, LoadMonitorActor.this)

  var numberOfSuccessfulLogins = 0

  def nervous: Receive = {
    case "SEVERE" => log.error("still very nervous")
    case "INFO" => {
      log.info("getting relaxed")
      become(relaxed)
    }
  }

  def relaxed: Receive = {
    case "SEVERE" => {
      log.error("getting nervous")
      become(nervous)
    }
    case "INFO" => log.error("relaxed")
  }

  def receive = {
    case "SEVERE" => become(nervous)
    case "INFO" => become(relaxed)
  }
}

object becomeExample extends App {

  val system = ActorSystem("mySystem")

  val monitorActor = system.actorOf(Props[LoadMonitorActor])
  
  monitorActor ! "INFO"
  monitorActor ! "INFO"
  monitorActor ! "SEVERE"
  monitorActor ! "SEVERE"
  monitorActor ! "INFO"

  // an Actor System needs to be shut down
  Console.readLine(s"Hit return to stop\n")
  println("I am done, goodbye")
  system.shutdown()

}









  
