package sigil

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.http.scaladsl.server.Route
import sigil.api.v1.{
  ConstraintRoutes,
  DistributionRoutes,
  FlagRoutes,
  SegmentRoutes,
  TagRoutes,
  VariantRoutes
}
import sigil.service.FlagService
import sigil.service.impl.FlagServiceImpl

object RootActor {
  sealed trait Command
  case object Stop extends Command

  def apply(): Behavior[Command] = Behaviors.receiveMessage {
    case Stop => Behaviors.stopped
  }
}

object Main {
  def createRoutes(): Route = {
    import akka.http.scaladsl.server.Directives._

//    val flagService = new FlagServiceImpl()
    (new ConstraintRoutes).route ~
//      (new FlagRoutes).route ~
      (new DistributionRoutes).route ~
      (new SegmentRoutes).route ~
      (new TagRoutes).route ~
      (new VariantRoutes).route
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem(RootActor(), "sigil")

  }
}
