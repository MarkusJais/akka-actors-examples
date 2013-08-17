package com.markusjais.scala.akka.examples.basics

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem

class AnimalActor extends Actor {
  import AnimalActor._
  val log = Logging(context.system, AnimalActor.this)
  def receive = {
    case Cat(name) => log.info(s"got a cat: $name")
    case Eagle(name) => log.info(s"got an eagle: $name")
    case _ => log.warning("don't know this stuff")
  }
}

object AnimalActor {
  sealed trait Animal
  case class Cat(name: String) extends Animal
  case class Eagle(name: String) extends Animal
}

object simpleActorExample extends App {

  import AnimalActor._

  // actors live inside an Actor System
  val system = ActorSystem("mySystem")

  val animalActor = system.actorOf(Props[AnimalActor])

  // ! is a method to send messages to an actor
  animalActor ! Eagle("African Crowned Eagle")
  animalActor ! Cat("Clouded Leopard")
  animalActor ! 42

  // an Actor System needs to be shut down
  Console.readLine(s"Hit return to stop")
  println("I am done, goodbye")
  system.shutdown()

}





