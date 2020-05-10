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

Some changes that were not refered by the documentation are added here

1. in `Stage.scala`


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

1. in `AbstractUI.scala`

```scala
class AbstractUI {
  private[this] var state = newState(Block((0, 0), TKind) :: Nil,
    randomStream(new util.Random))

  private[this] def randomStream(random: util.Random): LazyList[PieceKind] =

...
```

1. in `Main.scala`

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

:warning: `StageSpec.scala` has changed drastically

