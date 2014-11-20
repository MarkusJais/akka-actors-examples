package com.markusjais.scala.akka.examples.become

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import akka.event.Logging
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.util.Success
import scala.util.Failure

class LoadMonitorActor extends Actor with ActorLogging {
  import context._

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
    case "INFO" => log.info("relaxed")
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









  
