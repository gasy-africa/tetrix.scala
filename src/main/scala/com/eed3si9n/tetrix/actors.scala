package com.eed3si9n.tetrix

import akka.actor.Actor

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