package com.eed3si9n.tetrix

import Stage._
import java.{util => ju}

class AbstractUI {
  private[this] var state = newState(Block((0, 0), TKind) :: Nil,
    randomStream(new util.Random))

  private[this] def randomStream(random: util.Random): LazyList[PieceKind] =
    PieceKind(random.nextInt % 7) #:: randomStream(random)

  private[this] val timer = new ju.Timer
  timer.scheduleAtFixedRate(new ju.TimerTask {
    def run() { state = tick(state) }
  }, 0, 1000)

  def left(): Unit = {
    state = moveLeft(state)
  }

  def right(): Unit = {
    state = moveRight(state)
  }
  def up(): Unit = {
    state = rotateCW(state)
  }
  def down(): Unit = {
    state = tick(state)
  }
  def space(): Unit = {
    state = drop(state)
  }
  def view: GameView = state.view
}