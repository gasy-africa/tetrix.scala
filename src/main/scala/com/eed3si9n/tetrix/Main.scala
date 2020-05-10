package com.eed3si9n.tetrix

import javax.swing.{AbstractAction, Timer}

import swing._
import event._

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

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

    val uiView: Future[GameView] = ui.view

    // TODO: Blocking Code
    import scala.language.postfixOps
    import scala.concurrent.duration._
    import akka.util.Timeout
    val view = Await.result(uiView, Timeout(1 second).duration)

    // TODO: Blocking Code
//    Thread.sleep(100)
//    for (view <- uiView)
//         drawBoard(g, (0, 0), view.gridSize, view.blocks, view.current)
      {
        drawBoard(g, (0, 0), (10, 20), view.blocks, view.current)
        drawBoard(g, (12 * (blockSize + blockMargin), 0), view.miniGridSize, view.next, Nil)
      }

    view.status match {
      case GameOver =>
        g setColor bluishSilver
        g drawString ("game over",
          12 * (blockSize + blockMargin), 7 * (blockSize + blockMargin))
      case _ => // do nothing
    }

  }

  def drawBoard(g: Graphics2D, offset: (Int, Int), gridSize: (Int, Int),
                blocks: Seq[Block], current: Seq[Block]) {

    val (colSize: Int, rowSize: Int) = gridSize
    val (colOffset: Int, rowOffset: Int) = offset

    def buildRect(pos: (Int, Int)): Rectangle = {
      val (col: Int, row: Int) = pos
      new Rectangle(colOffset + col * (blockSize + blockMargin),
        rowOffset + (rowSize - row - 1) * (blockSize + blockMargin),
        blockSize, blockSize)
    }

    def drawEmptyGrid() {
      g setColor bluishLighterGray
      for {
        x <- 0 until colSize
        y <- 0 until rowSize
        pos = (x, y)
      } g draw buildRect(pos)
    }

    def drawBlocks() {
      g setColor bluishEvenLighter
      blocks filter {_.pos._2 < rowSize} foreach { b =>
        g fill buildRect(b.pos) }
    }

    def drawCurrent() {
      g setColor bluishSilver
      current filter {_.pos._2 < rowSize}  foreach { b =>
        g fill buildRect(b.pos) }
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
    val timer = new Timer(100, new AbstractAction() {
      def actionPerformed(e: java.awt.event.ActionEvent) { repaint }
    })
    timer.start()
  }

}
