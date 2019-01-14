package com.kafka.websocket.poc

import akka.actor._
import spray.json.DefaultJsonProtocol._
import spray.json._

object CustomerCare {
  case class Join(id:String)
  case class RouteMessage(query: String, executive: String, customer: String = "" )
  case class ExecutiveExists(id:String)
  case class Result(contains:Boolean)
  implicit val RouteMessageFormat = jsonFormat3(RouteMessage)
  def parseToRouteMessage(s:String): RouteMessage ={
    s.parseJson.convertTo[RouteMessage]
  }
}

class CustomerCare extends Actor {
  import CustomerCare._
  //var users: Set[ActorRef] = Set.empty
  var users:Map[String,ActorRef] = Map.empty
  def receive = {
    case Join(id) => {
      if(users.contains(id)) users.get(id).get ! PoisonPill
      users += id -> sender()
      // we also would like to remove the user when its actor is stopped
      context.watch(sender())
    }

    case Terminated(user) =>{
      //users -= user
      println("actor terminated")
      users -= users.find(_._2==user).get._1
    }


    case msg: RouteMessage =>{
      println("Executive ============> "+msg.executive)
      //users.foreach(_._2 ! msg)
      //users.get(msg.executive).map(_ ! msg)
      users.get(msg.executive).map(_ ! RouteMessage(model.Data.sampleData.get(msg.query).get,msg.executive,msg.customer))
    }
    case ExecutiveExists(id)=>{sender ! Result(users.contains(id))}

  }
}
