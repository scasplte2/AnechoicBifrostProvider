package anechoic

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

/**
 * this is an abstract portal representing the construct of a portal to another node.
 *
 * concrete portal implementations can utilize any network protocol desired or even a local protocol between actors
 * for use in testing. importantly, the implementation of these different portals should be agnostic of the messages
 * being sent, they are for all intensive purposes, ignorable middleware used to make the abstract concept of a
 * connection between two nodes concrete.
 */
abstract class Portal extends Actor {

  val log = Logging(context.system, this)

  var handler: Option[ActorRef] = None

  def local2remote(message: String)

  def remote2local(message: String)

  def receive: Receive = {
    case "PROP-2-REMOTE" =>  local2remote("PROP-2-REMOTE") // TODO : implement message case class
    case "PROP-2-LOCAL" => remote2local("PROP-2-LOCAL") // TODO : implement message case class
    case "BIND-HANDLER" => this.handler = Some(sender())
    case _ => log.info("received unknown message")
  }

}
