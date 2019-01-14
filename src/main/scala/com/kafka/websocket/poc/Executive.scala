package com.kafka.websocket.poc

import akka.actor.{Actor, ActorRef}

object Executive {
  case class Connected(outgoing: ActorRef)
  case class IncomingMessage(text: String)
  case class OutgoingMessage(text: String)

}

class Executive(customerCareActor: ActorRef, kafkaProducerActor:ActorRef, exId:String) extends Actor {
  import Executive._

  def receive = {
    case Connected(outgoing) =>
      context.become(connected(outgoing))
  }

  def connected(outgoing: ActorRef): Receive = {
    customerCareActor ! CustomerCare.Join(exId)
    kafkaProducerActor ! KafkaMesageProducer.UpdateExecutives(List(exId),Left(1))

    {
      case IncomingMessage(text) =>
        customerCareActor ! CustomerCare.RouteMessage(text, exId)

      case CustomerCare.RouteMessage(text,id,cust) =>
        outgoing ! OutgoingMessage(text)
    }
  }

}
