package com.eed3si9n.tetrix

class Agent {

  import Stage._

  private[this] val minUtility = -1000.0

  def utility(state: GameState): Double =
    if (state.status == GameOver) -1000.0
    else state.lineCount.toDouble

  def bestMove(s0: GameState): StageMessage = {
    var retval: StageMessage = MoveLeft
    var current: Double = minUtility
    possibleMoves foreach { move =>
      val u = utility(toTrans(move)(s0))
      if (u > current) {
        current = u
        retval = move
      } // if
    }
    retval
  }

  private[this] val possibleMoves: Seq[StageMessage] =
    Seq(MoveLeft, MoveRight, RotateCW, Tick, Drop)

  private[this] def toTrans(message: StageMessage): GameState => GameState =
    message match {
      case MoveLeft  => moveLeft
      case MoveRight => moveRight
      case RotateCW  => rotateCW
      case Tick      => tick
      case Drop      => drop
    }

}
