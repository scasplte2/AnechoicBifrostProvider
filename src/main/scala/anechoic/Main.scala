package anechoic

import akka.actor.{ Props, ActorSystem }
import language.postfixOps

object Main extends App {
  val system = ActorSystem("mySystem")
  val core = system.actorOf(Props[Core], name="Core")
  val network = system.actorOf(Props[Network], name="network")
  core ! Message("STATE=>INITIALIZATION", None)
  network ! Message("STATE=>INITIALIZATION", None)
}
