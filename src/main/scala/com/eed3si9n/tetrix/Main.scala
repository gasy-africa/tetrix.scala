package com.eed3si9n.tetrix

import swing._
import event._

object Main extends SimpleSwingApplication {
  import event.Key._
  import java.awt.{Dimension, Graphics2D}
  import java.awt.{Color => AWTColor}

  val bluishGray = new AWTColor(48, 99, 99)
  val bluishSilver = new AWTColor(210, 255, 255)
  val bluishLighterGray = new AWTColor(79, 130, 130)
  val bluishEvenLighter = new AWTColor(145, 196, 196)
  val blockSize = 16
  val blockMargin = 1

  val ui = new AbstractUI

  def onKeyPress(keyCode: Value): Unit = keyCode match {
    case Left  => ui.left()
    case Right => ui.right()
    case Up    => ui.up()
    case Down  => ui.down()
    case Space => ui.space()
    case _ =>
  }

  def onPaint(g: Graphics2D) {

    val view = ui.view
    val (colSize: Int, rowSize: Int) = view.gridSize

    def buildRect(pos: (Int, Int)): Rectangle = {
      val (col: Int, row: Int) = pos
      new Rectangle(col * (blockSize + blockMargin),
        (rowSize - row - 1) * (blockSize + blockMargin),
        blockSize, blockSize)
    }

    def drawEmptyGrid() {
      g setColor bluishLighterGray
      for {
        x <- 0 until colSize
        y <- 0 to rowSize - 2
        pos = (x, y)
      } g draw buildRect(pos)
    }

    def drawBlocks() {
      g setColor bluishEvenLighter
      view.blocks foreach { b => g fill buildRect(b.pos) }
    }

    def drawCurrent() {
      g setColor bluishSilver
      view.current foreach { b => g fill buildRect(b.pos) }
    }

    drawEmptyGrid()
    drawBlocks()
    drawCurrent()

  }

  def top: MainFrame = new MainFrame {
    title = "tetrix"
    contents = mainPanel
  }

  def mainPanel: Panel = new Panel {
    preferredSize = new Dimension(700, 400)
    focusable = true
    listenTo(keys)
    reactions += {
      case KeyPressed(_, key, _, _) =>
        onKeyPress(key)
        repaint
    }
    override def paint(g: Graphics2D) {
      g setColor bluishGray
      g fillRect (0, 0, size.width, size.height)
      onPaint(g)
    }
  }

}
