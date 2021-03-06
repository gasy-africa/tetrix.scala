## :a: Journey

#### Day :two:

on [Day2](http://eed3si9n.com/tetrix-in-scala/day2.html) I was looking for a fix of the `leftWall1` spec test but couldn't fix the issue with the day2 documentation. I started poking around and realized that Eugene must have not slept that night because the `Stage` class went from a class to a companion object and finally a State Monad.

I decided to take a partial copy of the [`Stage.scala`](https://github.com/eed3si9n/tetrix.scala/blob/day2/library/src/main/scala/main/com/tetrix/Stage.scala) file produced overnight, change the `Stage` class in `AbstractUI.scala` to the new state monad, added the `GameState` case class to the `pieces.scala` file then fixed the `StageSpec.scala`. I created a `Day2` tag based on the overnight event.

I then tried to implement the [`rotation`](http://eed3si9n.com/tetrix-in-scala/rotation.html) and realized that there was a [`refactoring`](http://eed3si9n.com/tetrix-in-scala/refactoring.html) explaining the State Monad fix. Well, I didn't want to go ahead of myself when learning the game but may be I should have had.

Before implementing the `collision detection`, the [`Day2`](http://eed3si9n.com/tetrix-in-scala/day2.html) page should have had a `validate` function with its `inBounds` inner fonction written like this. The `moveBy` method is wrong since it doesn't implement the new State Monad.

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

Some changes that were not refered in the documentation were added here

1. in `Stage.scala`


```scala
  def newState(blocks: Seq[Block], kinds: Seq[PieceKind]): GameState = {
    val size = (10, 20)
    val dummy = Piece((0, 0), TKind)
    val withNext = spawn(GameState(Nil, size, dummy, dummy, kinds)).copy(blocks = blocks)
    spawn(withNext)
  }
```

`Function.chain` is now chaining two funtions of the same type `clearFullRow` and `spawn` take the `GameState` State Monad as parameter

```scala
  val tick: GameState => GameState = transit(_.moveBy(0.0, -1.0), Function.chain(clearFullRow :: spawn :: Nil) )
```

2. in `AbstractUI.scala`

```scala
class AbstractUI {
  private[this] var state = newState(Block((0, 0), TKind) :: Nil,
    randomStream(new util.Random))

  private[this] def randomStream(random: util.Random): LazyList[PieceKind] =

...
```

3. in `Main.scala`

```scala
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

#### Day :four:


:round_pushpin: Akka

https://www.reactivesystems.eu/2019/02/19/dont-use-awaitresult.html

I moved the `Await.result` blocking code from `AbstractUI` class below 

```
  def view: Future[GameView] = {
    import akka.util.Timeout
    import akka.pattern.ask
    implicit val timeout: Timeout = Timeout(1 second)
    (playerActor ? View).mapTo[GameView]
  }
```

to the `Main.scala` UI code and left the UI taking care of the blocking code 

```
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
         drawBoard(g, (0, 0), view.gridSize, view.blocks, view.current)

  }
```

:round_pushpin: Game Status nees to be added to the `GameView` and `GameState` case classes


```
case class GameView(blocks: Seq[Block], gridSize: (Int, Int),
  current: Seq[Block], miniGridSize: (Int, Int), next: Seq[Block],
  status: GameStatus)
```

```
case class GameState(blocks: Seq[Block], gridSize: (Int, Int),
    currentPiece: Piece, nextPiece: Piece, kinds: Seq[PieceKind],
    status: GameStatus) {
  def view: GameView = GameView(blocks, gridSize,
    currentPiece.current, (4, 4), nextPiece.current, status)
}
```

Add `ActiveStatus` when creating the State Monad in `Stage.scala`

```
  def newState(blocks: Seq[Block], kinds: Seq[PieceKind]): GameState = {
    val size = (10, 20)
    val dummy = Piece((0, 0), TKind)
    val withNext = spawn(GameState(Nil, size, dummy, dummy, kinds, ActiveStatus)).
      copy(blocks = blocks)
    spawn(withNext)
  }

```

:bulb: Tests:

when adding the last `spawn1` test ttt needs to be changed

```
  val ttt: Seq[PieceKind] = Nil padTo (20, TKind)
```

I added a `package object` in the `com.eed3si9n.tetrix` to declare a state Monad `type State[A] = A => A`

#### Day :six:

1. Add the `StateExample` Class in `$PROJECT/src/test/scala` folder

```scala
trait StateExample {
  import com.eed3si9n.tetrix._
  import Stage._

  def ttt: List[PieceKind] = Nil padTo (20, TKind)
  def s1: GameState = newState(Block((0, 0), TKind) :: Nil, (10, 20), ttt)
  def s2: GameState = newState(Block((3, 18), TKind) :: Nil, (10, 20), ttt)
  def s3: GameState = newState(Seq(
    (0, 0), (1, 0), (2, 0), (3, 0), (7, 0), (8, 0), (9, 0))
    map { Block(_, TKind) }, (10, 20), ttt)
  def s4: GameState = newState(Nil, (10, 20), OKind :: OKind :: Nil)
  def gameOverState: GameState = Function.chain(Nil padTo (10, drop))(s1)
}
```

## Day :seven:

Along with the `Day7` explanation, I changed the `Agent.scala` filename to `agents.scala` and added an `import` and a `minUtility` variable, as below:

```scala
class Agent {

  import Stage._

  private[this] val minUtility = -1000.0

...
```

I added `StateActor` along with its `StateMessage` ADT which was refered by the `GameMasterActor`

```scala
sealed trait StateMessage
case object GetState extends StateMessage
case class SetState(s: GameState) extends StateMessage
case object GetView extends StateMessage

class StateActor(s0: GameState) extends Actor {
  private[this] var state: GameState = s0

  def receive = {
    case GetState    => sender ! state
    case SetState(s) => state = s
    case GetView     => sender ! state.view
  }
}
```
### Day :eight: buggy-penalty

Adding `unload` function

Class `GameState` is missing `unload()` function in explanations when refering to `penalty` function

```scala
  def unload(p: Piece): GameState = {
    val currentPoss = p.current map {_.pos}
    this.copy(blocks = blocks filterNot { currentPoss contains _.pos })
  }
```
