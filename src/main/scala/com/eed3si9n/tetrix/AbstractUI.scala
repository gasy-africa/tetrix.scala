package com.eed3si9n.tetrix

import Stage._

class AbstractUI {
  private[this] var state = newState(Block((0, 0), TKind) :: Nil)

  def left() {
    state = moveLeft(state)
  }
  def right() {
    state = moveRight(state)
  }
  def up() {
  }
  def down() {
  }
  def space() {
  }
  def view: GameView = state.view
}