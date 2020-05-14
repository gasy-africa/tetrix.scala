package com.eed3si9n.tetrix

import akka.actor.{Actor, ActorRef}
import scala.concurrent.duration._
import scala.concurrent._
import scala.language.postfixOps
import ExecutionContext.Implicits.global
import akka.pattern.ask

sealed trait StageMessage
case object MoveLeft extends StageMessage
case object MoveRight extends StageMessage
case object RotateCW extends StageMessage
case object Tick extends StageMessage
case object Drop extends StageMessage
case object View extends StageMessage

class StageActor(s0: GameState) extends Actor {
  import Stage._

  private[this] val state: GameState = s0

  def receive: Receive = onMessage(state)

  private def onMessage(state: GameState): Receive = {
    case MoveLeft => context.become(onMessage(moveLeft(state)))
    case MoveRight => context.become(onMessage(moveRight(state)))
    case RotateCW => context.become(onMessage(rotateCW(state)))
    case Tick => context.become(onMessage(tick(state)))
    case Drop => context.become(onMessage(drop(state)))
    case View => sender ! state.view
  }
}

sealed trait AgentMessage
case class BestMove(s: GameState) extends AgentMessage

class AgentActor(stageActor: ActorRef) extends Actor {
  private[this] val agent = new Agent

  def receive: Receive = {
    case BestMove(s: GameState) =>
      val message = agent.bestMove(s)
      if (message != Drop) stageActor ! message
  }
}

class GameMasterActor(stateActor: ActorRef, agentActor: ActorRef) extends Actor {
  def receive: Receive = {
    case Tick =>
      val s = getState
      if (s.status != GameOver) {
        agentActor ! BestMove(getState)
      }
  }
  private[this] def getState: GameState = {
    val future = (stateActor ? GetState)(1 second).mapTo[GameState]
    // TODO: Blocking Code
    Await.result(future, 1 second)
  }
}

sealed trait StateMessage
case object GetState extends StateMessage
case class SetState(s: GameState) extends StateMessage
case object GetView extends StateMessage

class StateActor(s0: GameState) extends Actor {

  private[this] val state: GameState = s0

  def receive: Receive = onMessage(state)

  private def onMessage(state: GameState): Receive = {
    case GetState => sender ! state
    case SetState(s) => context.become(onMessage(s))
    case GetView => sender ! state.view
  }
}