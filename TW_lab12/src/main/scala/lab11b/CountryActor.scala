package lab11b

import akka.actor.{Actor, ActorRef, Cancellable, PoisonPill, Props}
import akka.event.Logging

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object CountryActor {
  def props(team: String, position: String, matchRef: ActorRef, teamRef: Array[ActorRef], enemyRef: Array[ActorRef]): Props =
    Props(new CountryActor(team, position, matchRef, teamRef, enemyRef))

  case object Stop

  case object Goal

  case object Defended

  case object Kick

  case object Attack

  case object Defend

  case object CaptureBall

  case object Lost

}

class CountryActor(team: String, val position: String, matchRef: ActorRef, teamRef: Array[ActorRef], enemyRef: Array[ActorRef]) extends Actor {

  import CountryActor._

  var hasBall = false
  var myResults = 0
  val log = Logging(context.system, this)
  var kickTask: Cancellable = _
  var defendTask: Cancellable = _

  def randomInt(start: Int, end: Int): Int = {
    new Random().nextInt(end - start) + start
  }

  def receive: Receive = {
    case Kick =>
      if (position == "GoalKeeper") {
        if (randomInt(1, 10) < 4) {
          log.info(team + ": I cannot defend the shoot")
          sender() ! CountryActor.Goal
        } else {
          log.info(team + ": I defended the shoot")
          sender() ! CountryActor.Defended
        }
        teamRef(randomInt(1, 10)) ! Kick
      } else if (position == "Player") {
        log.info("Team " + team + " received message kick")
        hasBall = true
        teamRef.foreach(a => if (a != self) {
          a ! Attack
        })
        val delay = randomInt(50, 300)
        kickTask = context.system.scheduler.scheduleOnce(delay milliseconds) {
          hasBall = false
          if (randomInt(1, 5) == 1) {
            enemyRef(0) ! Kick
            teamRef.foreach(a => if (a != self) {
              a ! Defend
            })
          } else {
            teamRef(randomInt(1, 10)) ! Kick
          }
        }

      }

    case Defended =>
      log.info("My shot was defended")

    case Goal =>
      log.info("I scored a goal")
      myResults += 1
      matchRef ! MatchActor.Goal(team)

    case CaptureBall =>
      if(randomInt(1,5)==2){

      }
      if (hasBall) {
        hasBall = false
        if (kickTask != null) kickTask.cancel()
        log.info(team+ ": ball captured")
        sender() ! Lost
        teamRef.foreach(a => if (a != self) {
          a ! Defend
        })
      }

    case Defend =>
      val defendDelay = randomInt(50, 300)
      defendTask = context.system.scheduler.scheduleOnce(defendDelay milliseconds) {
        enemyRef(randomInt(1, 10)) ! CaptureBall
      }

    case Lost =>
      teamRef.foreach(a => if (a != self) {
        a ! Attack
      })
      hasBall = true
      log.info(team + " takes the ball")
      teamRef(randomInt(1,10)) ! Kick

    case Attack =>
      if (defendTask != null) defendTask.cancel()

    case Stop =>
      log.info("Player stopping")
      self ! PoisonPill
      sender() ! MatchActor.Stop


    case _ => log.info("Received unknown message")
  }
}
