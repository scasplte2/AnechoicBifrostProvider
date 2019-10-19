package anechoic

import akka.actor.{ Actor, ActorRef }
import akka.event.Logging
import akka.util.Timeout
import anechoic.CoreModes.CoreModes

import language.postfixOps

object CoreModes extends Enumeration {
  type CoreModes = Value
  val START, INITIALIZATION, REPAIR, NORMAL = Value
}

class Core extends Actor {

  val log = Logging(context.system, this)

  var messageListeners: scala.collection.mutable.Set[ActorRef] = scala.collection.mutable.Set[ActorRef]()
  var mode: CoreModes = CoreModes.START
  var boundNetwork: Option[ActorRef] = None
  var corePortal: Option[ActorRef] = None

  def receive: Receive = {
    case Message("STATE_INITIALIZATION", _) => this.mode = CoreModes.INITIALIZATION

    case Message("STATE_REPAIR", _) => this.mode = CoreModes.REPAIR

    case Message("STATE_NORMAL", _) => this.mode = CoreModes.NORMAL

    case Message("BIND_NETWORK", networkActorRef) => this.mode match {
      case CoreModes.INITIALIZATION =>
        this.boundNetwork = Some(networkActorRef.get.asInstanceOf[ActorRef])
      case _ => log.error("received BIND_NETWORK message while not in INITIALIZATION mode")
    }

    case Message("CREATE_CORE_PORTAL", None) => this.mode match {
      case CoreModes.INITIALIZATION => this.boundNetwork match {
        case Some(network) =>
          network ! Message("CREATE_PORTAL", None)
        case None => log.error("received CREATE_CORE_PORTAL message before a network was bound")
      }
      case _ => log.error("received CREATE_CORE_PORTAL message while not in INITIALIZATION mode")
    }

    case Message("PORTAL_CREATED", portalOpt) => this.mode match {
      case _ => portalOpt match {
        case Some(portal) =>
          this.corePortal = Some(portal.asInstanceOf[ActorRef])
          this.corePortal.get ! Message("STATE=>LIVE", None)  // activate the portal
        case _ => log.error("received PORTAL_CREATED message without an attached portal actor ref")
      }
    }

    case Message("ATTACH_MESSAGE_LISTENER", messageListenerOpt) => this.mode match {
      case _ => messageListenerOpt match {
        case Some(messageListener) => this.messageListeners.add(messageListener.asInstanceOf[ActorRef])
        case _ => log.error("received ATTACH_MESSAGE_LISTENER message without an attached message listener actor ref")
      }
    }

    case Message("DETACH_MESSAGE_LISTENER", messageListenerOpt) => this.mode match {
      case _ => messageListenerOpt match {
        case Some(messageListener) => this.messageListeners.remove(messageListener.asInstanceOf[ActorRef])
        case _ => log.error("received DETACH_MESSAGE_LISTENER message without an attached message listener actor ref")
      }
    }

    case Message("CORE_MESSAGE", coreMessageOpt) => this.mode match {
      case CoreModes.NORMAL => coreMessageOpt match {
        case Some(coreMessage) => coreMessage match {
          // TODO: core level matching n' shit
          case _ => log.error("received CORE_MESSAGE message whose message was total non-sense")
        }
        case None => log.error("received CORE_MESSAGE message without an attached core message object")
      }
      case _ => log.error("received CORE_MESSAGE message while not in NORMAL mode")
    }







//    case m => this.state match {
//      case CoreState.START => m match {
//        case
//        case _ => _ // TODO
//      }
//      case CoreState.INITIALIZATION => m match {
//        case _ => _ // TODO
//      }
//      case CoreState.REPAIR => m match {
//        case _ => _ // TODO
//      }
//      case CoreState.NORMAL => m match {
//        case _ => _ // TODO
//      }
//    }


//    case "REGISTER-NETWORK" => this.network = Some(sender())
//    case "PROVISION-PORTAL" => this.network match {
//      case Some(networkActorRef) => networkActorRef ! "PROVISION-PORTAL"
//      case None => log.error("portal provisioning tried without a registered network")
//    }
//    case Message("PROVISIONED-PORTAL", portal) => log.info("prov portal TBI") // TODO
//    case _ => log.info("received unknown message")
  }

}
