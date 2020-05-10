package com.eed3si9n.tetrix

import Stage._
import java.{util => ju}

class AbstractUI {

  private[this] var state = newState(Block((0, 0), TKind) :: Nil,
    randomStream(new util.Random))

  private[this] def randomStream(random: util.Random): LazyList[PieceKind] =
    PieceKind(random.nextInt % 7) #:: randomStream(random)

  private[this] def updateState(trans: GameState => GameState) {
    synchronized {
      state = trans(state)
    }
  }

  private[this] val timer = new ju.Timer
  timer.scheduleAtFixedRate(new ju.TimerTask {
    def run() { updateState { tick } }
  }, 0, 1000)

  def left(): Unit = updateState{ moveLeft }

  def right(): Unit = updateState { moveRight }

  def up(): Unit = updateState { rotateCW }

  def down(): Unit = updateState { tick }

  def space(): Unit = updateState { drop }

  def view: GameView = state.view

}