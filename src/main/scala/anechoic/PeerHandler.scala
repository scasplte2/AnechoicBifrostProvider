package anechoic

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

class PeerHandler extends Actor {

  val log = Logging(context.system, this)

  var portal: Option[ActorRef] = None
  var peer: Option[ActorRef] = None

  override def receive: Receive = {
    case Message("REGISTER-PORTAL", portal) =>
      this.portal = Some(portal.asInstanceOf[ActorRef])  // save portal
      this.portal.get ! Message("BIND-PARENT", None)  // tell portal i'm its daddy
      this.peer match {
        case Some(peer) => this.portal.get ! Message("BIND-COUNTERPART", Some(portal))
      }
    case Message("REGISTER-PEER", peer) =>
      this.peer = Some(peer.asInstanceOf[ActorRef])
      this.portal match {
        case Some(portal) => portal ! Message("BIND-COUNTERPART", Some(portal))  // notify portal of peer
      }
    case _ => log.info("received unknown message")
  }

}
