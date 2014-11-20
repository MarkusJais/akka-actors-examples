package com.markusjais.scala.akka.examples.supervisionsimple

import akka.actor.Actor
import akka.event.Logging
import scala.concurrent.duration._
import scala.util.Random
import akka.actor.ActorRef
import akka.actor.SupervisorStrategy.{ Stop, Resume, Restart }

class OrderClientActor(orderServiceActor: ActorRef) extends Actor {

  val log = Logging(context.system, OrderClientActor.this)

  import OrderClientActor._

  implicit val ec = context.dispatcher
  val ticker = context.system.scheduler.schedule(1.second, 2.second, self, getNextOrderRequest)

  def receive = {

    case request: OrderRequest => {
      log.info("got Order request: " + request)
      orderServiceActor ! BookOrder(request.userId, request.books)
    }
    case _ => log.error("wrong message")
  }
}

object OrderClientActor {
  case class OrderRequest(userId: Long, books: List[Book])

  def getNextOrderRequest(): OrderRequest = {
    val jcip = Book("Java Concurrency in Practice", 12.0)
    val effJava = Book("Effective Java", 24.0)
    val progRuby = Book("Programming Ruby", 25.0)
    val railsTutorial = Book("The Rails  Tutorial", 22.0)

    val javaBookOrder = OrderRequest(42, List(jcip, effJava))
    val rubyBookOrder = OrderRequest(99, List(progRuby, railsTutorial))

    val randomInt = Random.nextInt(2)
    if(randomInt == 0) javaBookOrder else rubyBookOrder
  }
}





