package anechoic

import akka.actor.{ Actor, ActorRef, Props}
import akka.event.Logging

class Network extends Actor {

  val log = Logging(context.system, this)

  var core: Option[ActorRef] = None

  // TODO : implement [IP]
  def receive: Receive = {
    case "REGISTER-CORE" => this.core = Some(sender())
    case "EXTERNAL-MESSAGE" => this.core match {
      case Some(coreActorRef) => coreActorRef ! "EXTERNAL-MESSAGE"  // TODO : implement message case class
      case None => log.error("external message received without a registered core")
    }
    case "PROVISION-PORTAL" => sender() ! Message("PROVISIONED-PORTAL", Some(Props[LocalPortal]))
    case _ => log.info("received unknown message")
  }

}
