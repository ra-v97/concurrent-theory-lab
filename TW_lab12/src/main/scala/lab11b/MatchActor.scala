package lab11b

import akka.actor.{Actor, ActorRef, ActorSystem, Props, ReceiveTimeout}
import akka.event.Logging

import scala.concurrent.duration._
import scala.util.Random


object MatchActor {
  def props(duration: Int, interval: Double, stadium: String, systemRef: ActorSystem): Props =
    if (duration < 0 || interval <= 0)
      throw new RuntimeException("invalid timer's values")
    else
      Props(new MatchActor(duration, interval, stadium, systemRef))

  final case class Init(teamA: String, teamB: String)

  final case class Goal(team: String)

  case object Stop

}

class MatchActor(duration: Int, interval: Double, val stadium: String, systemRef: ActorSystem) extends Actor {

  import MatchActor._

  private val team_A: Array[ActorRef] = new Array[ActorRef](11)
  private val team_B: Array[ActorRef] = new Array[ActorRef](11)
  private var isRunning = false
  private var activePlayers = 0
  private var teamAResult = 0
  private var teamBResult = 0
  private var _teamA = ""
  private var _teamB = ""
  val log = Logging(context.system, this)
  //context.setReceiveTimeout(30 milliseconds)

  def init(teamA: String, teamB: String): Unit = {
    _teamA = teamA
    _teamB = teamB

    for (i <- 1 to 10) {
      team_A(i) = systemRef.actorOf(CountryActor.props(teamA, "Player", self, team_A, team_B), "Player" + i + "A")
      team_B(i) = systemRef.actorOf(CountryActor.props(teamB, "Player", self, team_B, team_A), "Player" + i + "B")
    }
    team_A(0) = systemRef.actorOf(CountryActor.props(teamA, "GoalKeeper", self, team_A, team_B), "GoalKeeper" + "A")
    team_B(0) = systemRef.actorOf(CountryActor.props(teamB, "GoalKeeper", self, team_B, team_A), "GoalKeeper" + "B")
    activePlayers = 22;
  }

  def stopAllPlayers() = {
    log.info("Stopping all players")
    for (i <- 0 to 10) {
      team_A(i) ! CountryActor.Stop
      team_B(i) ! CountryActor.Stop
    }
  }

  def randomInt(start: Int, end: Int): Int = {
    new Random().nextInt(end - start) + start
  }

  def receive: Receive = {

    case Init(teamA, teamB) =>
      if (!isRunning) {
        isRunning = true
        log.info("Creating teamA: " + teamA)
        log.info("Creating teamB: " + teamB)
        init(teamA, teamB)
        log.info("Init match by kick")
        if (randomInt(1, 2) == 1) {
          team_A(1) ! CountryActor.Kick
          team_B.foreach(a => a ! CountryActor.Defend)
        } else {
          team_B(1) ! CountryActor.Kick
          team_A.foreach(a => a ! CountryActor.Defend)
        }
        //context.setReceiveTimeout(100 milliseconds)
      } else {
        log.info("Already running")
      }

    case Goal(team) =>
      if (team == _teamA) {
        teamAResult += 1
      }
      if (team == _teamB) {
        teamBResult += 1
      }
    case Stop =>
      if (isRunning) {
        isRunning = false
        stopAllPlayers()
      } else {
        activePlayers -= 1
        if (activePlayers == 0) {
          log.info("Results: " + _teamA + "->" + teamAResult + " ; " + _teamB + "->" + teamBResult)
          log.info("Match end!")
          systemRef.terminate()
        }
      }

    case ReceiveTimeout ⇒
      context.setReceiveTimeout(Duration.Undefined)
      throw new RuntimeException("Receive timed out")

    case _ => log.info("Received unknown message")
  }
}
