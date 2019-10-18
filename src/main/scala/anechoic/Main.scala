package anechoic

import akka.actor.{ Props, ActorSystem }
import language.postfixOps

object Main extends App {
  val system = ActorSystem("mySystem")
  val inst = system.actorOf(Props[Core], name="Core")
  inst ! "test"
}
