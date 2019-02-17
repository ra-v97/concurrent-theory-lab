package lab11b

import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ApplicationMain extends App{

  val system = ActorSystem("footballMatch")
  val matchActor = system.actorOf(MatchActor.props(5,0.001,"ABC stadium",system), "matchActor")

  val teamA = "Poland"
  val teamB = "Germany"

  matchActor ! MatchActor.Init(teamA,teamB)

  system.scheduler.scheduleOnce(5 seconds, matchActor, MatchActor.Stop)
  Await.result(system.whenTerminated, Duration.Inf)
}
