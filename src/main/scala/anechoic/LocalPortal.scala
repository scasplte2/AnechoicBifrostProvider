package anechoic

import akka.actor.ActorRef
import akka.event.Logging

class LocalPortal extends Portal {

  var counterpart: Option[LocalPortal] = None

  override def local2remote(message: String): Unit = this.counterpart match {
    case Some(counterpart) => counterpart.remote2local(message)
    case None => log.error("attempted portal use before counterpart bound")
  }

  override def remote2local(message: String): Unit = this.handler match {
    case Some(handler) => handler ! message  // no serialization for local portals (special case not the norm)
    case None => log.error("attempted portal use before counterpart bound")
  }

  override def receive: Receive = {
    case "BIND-COUNTERPART" => this.counterpart = Some(sender().asInstanceOf[LocalPortal])
    case generalMessage => super.receive(generalMessage)
  }

}
