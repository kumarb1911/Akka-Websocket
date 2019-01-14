package com.kafka.websocket.poc

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.pattern.ask
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout
import com.kafka.websocket.poc.model.JsonSupport
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.Await
import scala.concurrent.duration._


object Server extends Directives with JsonSupport{
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()

    val confFactory = ConfigFactory.load()

    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
     // .withBootstrapServers(confFactory.getString("akka.kafka."))

    val consumerSettings = ConsumerSettings(
      system = system,
      keyDeserializer = new ByteArrayDeserializer,
      valueDeserializer = new StringDeserializer)
      .withGroupId("user-consumer-group")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //.withBootstrapServers("localhost:9093")

    val customerCare = system.actorOf(Props(new CustomerCare), "custCare")

    val kafkaProducerActor = system.actorOf(KafkaMesageProducer.props(producerSettings),"kp")



    // (1) this is one way to "ask" another actor
    implicit val timeout = Timeout(5 seconds)



    def newExecutive(id:String): Flow[Message, Message, NotUsed] = {
      val executiveActor = system.actorOf(Props(new Executive(customerCare,kafkaProducerActor,id)))

      val incomingMessages: Sink[Message, NotUsed] =
        Flow[Message].map {
          case TextMessage.Strict(text) => Executive.IncomingMessage(text)
        }.to(Sink.actorRef[Executive.IncomingMessage](executiveActor, PoisonPill))

      val outgoingMessages: Source[Message, NotUsed] =
        Source.actorRef[Executive.OutgoingMessage](10, OverflowStrategy.fail)
        .mapMaterializedValue { outActor =>
          executiveActor ! Executive.Connected(outActor)
          NotUsed
        }.map(
          (outMsg: Executive.OutgoingMessage) => TextMessage(outMsg.text))

      Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
    }


    def readMessages(): Unit =
      for (ln <- io.Source.stdin.getLines) ln match {
        case "" =>
          system.terminate()
          return
        case other => customerCare ! CustomerCare.RouteMessage("Oter -> "+other, other)
      }

    def Consume():Unit={
      val source = Consumer.plainSource(consumerSettings, Subscriptions.topics(ConfigFactory.load().getString("app.kafka.topic")))
        .map(consumerRecord => consumerRecord.value())
        .map(k=>{
          val message:CustomerCare.RouteMessage = CustomerCare.parseToRouteMessage(k)
          customerCare ! message
        })
        .runWith(Sink.ignore)
    }

    val route =
      get{
        path(""){
          getFromResource("public/html/login.html")
        }
      } ~
      get {
        path("join"/Segment) {executiveId =>
          handleWebSocketMessages(newExecutive(executiveId))
        }
      } ~
        get {
          pathPrefix("assets"){
            getFromResourceDirectory("public")
          }
        } ~
        get{
          path("agentexists"/Segment){agentname=>
            val result = Await.result(customerCare ? CustomerCare.ExecutiveExists(agentname),5.seconds).asInstanceOf[CustomerCare.Result]
            complete(result.contains.toString.toLowerCase())
          }
        } ~
        get{
            path("login"){
                getFromResource("public/html/client.html")
            }
        }

    val binding = Await.result(Http().bindAndHandle(route, "127.0.0.1", 8080), 3.seconds)


    // the rest of the sample code will go here
    println("Started server at 127.0.0.1:8080, press enter to kill server")
    Consume()
    val cancel = system.scheduler.schedule(Duration(5,TimeUnit.SECONDS),Duration(30,TimeUnit.SECONDS),kafkaProducerActor,KafkaMesageProducer.ProduceMessage)
    readMessages()
  }
}
