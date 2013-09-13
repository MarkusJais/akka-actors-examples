package com.markusjais.scala.akka.examples.supervision

import akka.actor.ActorSystem
import akka.actor.Props

object supervisorExample extends App {

  val system = ActorSystem("mySystem")
 // val mySqlConnection = new MysqlConnection()
  
  val writerProps = Props(new OrderWriterActor)
  
  val writerSupervisorProps = Props(new WriterSupervisor(writerProps))
  
  
  val processorProps = Props(
    new ProcessorSupervisor(writerSupervisorProps))
    
    
  val readerSupervisorProps = Props(new ReaderSupervisor(
    processorProps))
    
  system.actorOf(readerSupervisorProps)

  // an Actor System needs to be shut down
  Console.readLine(s"Hit return to stop")
  println("I am done, goodbye")
  system.shutdown()

}