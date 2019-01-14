package com.kafka.websocket.poc

import akka.actor.{Actor, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json.DefaultJsonProtocol._
import spray.json._

/**
  * Created by suresh on 10-01-2019.
  */
object KafkaMessage {
  case class IncomingCall(customer: String, executive: String,query:String)
  implicit val IncomingCallFormat = jsonFormat3(IncomingCall)

  implicit val callJsonWriter = new JsonWriter[IncomingCall] {
    def write(call: IncomingCall): JsValue = {
      JsObject(
        "customer" -> JsString(call.customer),
        "executive" -> JsString(call.executive),
        "query" -> JsString(call.query)
      )
    }
  }
  def parseToIncomingCall(s:String): IncomingCall ={
    s.parseJson.convertTo[IncomingCall]
  }
}
object KafkaMesageProducer{
  case object ProduceMessage
  case class UpdateExecutives(executivesList:List[String],action:Either[Int,Int])
  val config = ConfigFactory.load()
  def props(producerSettings:ProducerSettings[Array[Byte], String])(implicit  mterializer:ActorMaterializer):Props={
    Props(new KafkaMessageProducer(producerSettings))
  }
}
class KafkaMessageProducer(producerSettings:ProducerSettings[Array[Byte], String])(implicit  materializer:ActorMaterializer) extends Actor{
  import KafkaMesageProducer._
  val customers:List[String]=List("John","David","Steven","Mark","Antony")
  var executives:List[String]=List.empty
  val queries = List("homeloan","carloan","creditcard","propertyloan","commercialloan")
  val r = new scala.util.Random
  override def receive ={
    case ProduceMessage =>{
      if(!executives.isEmpty) {
        val done = Source(1 to 1)
          .map(_.toString)
          .map { elem =>
            new ProducerRecord[Array[Byte], String](KafkaMesageProducer.config.getString("app.kafka.topic"), KafkaMessage.IncomingCall(
              customers(r.nextInt(customers.length)),
              executives(r.nextInt(executives.length)),
              queries(r.nextInt(queries.length))
            ).toJson.toString())
          }
          .runWith(Producer.plainSink(producerSettings))
      }
      else
        println("No Executives Joined ...")
    }
    case UpdateExecutives(executivesList:List[String],action:Either[Int,Int]) =>{
      println("Updating executivesList ....")
      action match {
        case Left(a)  => {
          executives = executives ::: (executivesList diff executives)
          println(executives)
        }
        case Right(b) => executives = executives diff executivesList
      }
    }

    case _ =>{println("Unknown Message !!")}
  }
}




