import org.specs2._

class StageSpec extends Specification { def is = s2"""
  This is a specification to check Stage

  Moving to the left the current piece should
    change the blocks in the view.                                            $left1

  Moving to the right the current piece should
    change the blocks in the view.                                            $right1

  Moving to the left the current piece should
    change the blocks in the view                                             $left1
    as long as it doesn't hit the wall.                                       $leftWall1
                                                                              """
  import com.eed3si9n.tetrix._
  import Stage._
  val s1: GameState = newState(Block((0, 0), TKind) :: Nil)
  val s2: GameState = newState(Block((3, 17), TKind) :: Nil)
  val s3: GameState = newState(Seq(
    (0, 0), (1, 0), (2, 0), (3, 0), (7, 0), (8, 0), (9, 0))
    map { Block(_, TKind) })
  def left1 =
    moveLeft(s1).blocks map {_.pos} must contain(
      (0, 0), (3, 17), (4, 17), (5, 17), (4, 18)
    ).inOrder
  def right1 =
    moveRight(s1).blocks map {_.pos} must contain(
      (0, 0), (5, 17), (6, 17), (7, 17), (6, 18)
    ).inOrder
  def leftWall1 =
    Function.chain(moveLeft :: moveLeft :: moveLeft ::
      moveLeft :: moveLeft :: Nil)(s1).
      blocks map {_.pos} must contain(
      (0, 0), (0, 17), (1, 17), (2, 17), (1, 18)
    ).inOrder
}