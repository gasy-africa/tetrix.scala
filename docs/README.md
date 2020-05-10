## :a: Journey

#### Day :two:

on [Day2](http://eed3si9n.com/tetrix-in-scala/day2.html) I was looking for a fix of the `leftWall1` spec test but couldn't fix the issue with the day2 documentation. I started poking around and realized that Eugene must have not slept that night because the `Stage` class went from a class to a companion object and finally a State Monad.

I decided to take a partial copy of the [`Stage.scala`](https://github.com/eed3si9n/tetrix.scala/blob/day2/library/src/main/scala/main/com/tetrix/Stage.scala) file produced overnight, change the `Stage` class in `AbstractUI.scala` to the new state monad, added the `GameState` case class to the `pieces.scala` file then fixed the `StageSpec.scala`. I created a `Day2` tag based on the overnight event.

I then tried to implement the [`rotation`](http://eed3si9n.com/tetrix-in-scala/rotation.html) and realized that there was a [`refactoring`](http://eed3si9n.com/tetrix-in-scala/refactoring.html) explaining the State Monad fix. Well, I didn't want to go ahead of myself during in learning the game but may be I should have had.

Before implementing the `colision detection`, the [`Day2`](http://eed3si9n.com/tetrix-in-scala/day2.html) page should have a `validate` function with its `inBounds` inner fonction written like this. The `moveBy` method is wrong since it doesn't implement the new State Monad.

```scala
    private[this] def validate(s: GameState): Option[GameState] = {
      val size = s.gridSize
      def inBounds(pos: (Int, Int)): Boolean =
        (pos._1 >= 0) && (pos._1 < size._1) && (pos._2 >= 0) && (pos._2 < size._2)
      if (s.currentPiece.current map {_.pos} forall inBounds) Some(s)
      else None
    }
```

#### Day :three: 

in `Stage.scala`


```scala
  def newState(blocks: Seq[Block], kinds: Seq[PieceKind]): GameState = {
    val size = (10, 20)
    val dummy = Piece((0, 0), TKind)
    val withNext = spawn(GameState(Nil, size, dummy, dummy, kinds)).copy(blocks = blocks)
    spawn(withNext)
  }
```

`Function.chain` is now chaining two funtions of the same type `clearFullRow` and `spawn` take the `GameStae` State Monad as parameter

```
  val tick: GameState => GameState = transit(_.moveBy(0.0, -1.0), Function.chain(clearFullRow :: spawn :: Nil) )
```

in AbstractUI.scala

```scala
class AbstractUI {
  private[this] var state = newState(Block((0, 0), TKind) :: Nil,
    randomStream(new util.Random))

  private[this] def randomStream(random: util.Random): LazyList[PieceKind] =

...
```

in Main.scala

```
  def onPaint(g: Graphics2D) {
    val view = ui.view
    drawBoard(g, (0, 0), view.gridSize, view.blocks, view.current)
    drawBoard(g, (12 * (blockSize + blockMargin), 0),
      view.gridSize, view.next, Nil)
  }

  def drawBoard(g: Graphics2D, offset: (Int, Int), gridSize: (Int, Int),
                blocks: Seq[Block], current: Seq[Block]) {

    val view = ui.view
    val (colSize: Int, rowSize: Int) = view.gridSize
```

StageSpec has changed drastically

```
import org.specs2._

class StageSpec extends Specification { def is = s2"""
  This is a specification to check Stage

  Moving to the left the current piece should
    change the blocks in the view.                                            $left1

  Moving to the right the current piece should
    change the blocks in the view.                                            $right1

  Moving to the left the current piece should
    change the blocks in the view                                             $left1
    as long as it doesn't hit the wall                                        $leftWall1
    or another block in the grid.                                             $leftHit1

  Ticking the current piece should
    change the blocks in the view,                                            $tick1
    or spawn a new piece when it hits something                               $tick2
    It should also clear out full rows.                                       $tick3

  The current piece should
    be initialized to the first element in the state.                         $init1
                                                                              """
  import com.eed3si9n.tetrix._
  import Stage._

  val ttt: Seq[PieceKind] = TKind :: TKind :: TKind :: Nil

  val s1: GameState = newState(Block((0, 0), TKind) :: Nil, ttt)
  def left1 =
    moveLeft(s1).blocks map {_.pos} must contain(exactly(
      (0, 0), (3, 18), (4, 18), (5, 18), (4, 19)
    )).inOrder
  def right1 =
    moveRight(s1).blocks map {_.pos} must contain(exactly(
      (0, 0), (5, 18), (6, 18), (7, 18), (6, 19)
    )).inOrder
  def leftWall1 =
    Function.chain(moveLeft :: moveLeft :: moveLeft ::
      moveLeft :: moveLeft :: Nil)(s1).
      blocks map {_.pos} must contain(exactly(
      (0, 0), (0, 18), (1, 18), (2, 18), (1, 19)
    )).inOrder
  def rotate1 =
    rotateCW(s1).blocks map {_.pos} must contain(exactly(
      (0, 0), (5, 19), (5, 18), (5, 17), (6, 18)
    )).inOrder
  val s2 = newState(Block((3, 18), TKind) :: Nil, ttt)
  def leftHit1 =
    moveLeft(s2).blocks map {_.pos} must contain(exactly(
      (3, 18), (4, 18), (5, 18), (6, 18), (5, 19)
    )).inOrder
  def tick1 =
    tick(s1).blocks map {_.pos} must contain(exactly(
      (0, 0), (4, 17), (5, 17), (6, 17), (5, 18)
    )).inOrder
  def tick2 =
    Function.chain(Nil padTo (19, tick))(s1).
      blocks map {_.pos} must contain(exactly(
      (0, 0), (4, 0), (5, 0), (6, 0), (5, 1),
      (4, 18), (5, 18), (6, 18), (5, 19)
    )).inOrder

  val s3 = newState(Seq(
    (0, 0), (1, 0), (2, 0), (3, 0), (7, 0), (8, 0), (9, 0))
    map { Block(_, TKind) }, ttt)
  def tick3 =
    Function.chain(Nil padTo (19, tick))(s3).
      blocks map {_.pos} must contain(exactly(
      (5, 0), (4, 18), (5, 18), (6, 18), (5, 19)
    )).inOrder

  val s4 = newState(Nil, OKind :: OKind :: Nil)
  def init1 =
    (s4.currentPiece.kind must_== OKind) and
      (s4.blocks map {_.pos} must contain(exactly(
        (4, 18), (5, 18), (4, 17), (5, 17)
      )).inOrder)
}
```
