package com.eed3si9n.tetrix

import akka.actor._
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.language.postfixOps

class AbstractUI {
    private[this] val state = Stage.newState(Block((0, 0), TKind) :: Nil,
    randomStream(new scala.util.Random))

  private[this] def randomStream(random: scala.util.Random): LazyList[PieceKind] =
    PieceKind(random.nextInt % 7) #:: randomStream(random)

  private[this] val system = ActorSystem("TetrixSystem")

  private[this] val playerActor = system.actorOf(Props(new StageActor(state)), name = "playerActor")

  private[this] val timer = system.scheduler.scheduleWithFixedDelay(
    0 millisecond, 1000 millisecond, playerActor, Tick)

  def left(): Unit = { playerActor ! MoveLeft }

  def right(): Unit = { playerActor ! MoveRight }

  def up(): Unit = { playerActor ! RotateCW }

  def down(): Unit = { playerActor ! Tick }

  def space(): Unit = { playerActor ! Drop }

  def view: Future[GameView] = {
    import akka.util.Timeout
    import akka.pattern.ask
    implicit val timeout: Timeout = Timeout(1 second)
    (playerActor ? View).mapTo[GameView]
  }

}