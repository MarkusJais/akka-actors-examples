package com.markusjais.scala.akka.examples.supervisionsimple

import akka.actor.ActorSystem
import akka.actor.Props

object supervisorSimpleExample extends App {

  val system = ActorSystem("mySystem")

  val supervisorProps = Props(new OrderSupervisor)
  
  val superVisor = system.actorOf(supervisorProps)

  superVisor ! "init"

  // an Actor System needs to be shut down
  Console.readLine(s"Hit return to stop")
  println("I am done, goodbye")
  system.shutdown()

}