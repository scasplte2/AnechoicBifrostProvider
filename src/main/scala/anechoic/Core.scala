package anechoic

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

import language.postfixOps

class Core extends Actor {

  val log = Logging(context.system, this)
  val peerHandlers: Set[ActorRef] = Set()  // TODO : implement
  val corePort: Int = 5000  // TODO : make configurable

  var network: Option[ActorRef] = None // TODO : implement [IP]

  // TODO : implement [IP]
  def receive: Receive = {
    case "REGISTER-NETWORK" => this.network = Some(sender())
    case "PROVISION-PORTAL" => this.network match {
      case Some(networkActorRef) => networkActorRef ! "PROVISION-PORTAL"
      case None => log.error("portal provisioning tried without a registered network")
    }
    case Message("PROVISIONED-PORTAL", portal) => // TODO
    case _ => log.info("received unknown message")
  }

}
