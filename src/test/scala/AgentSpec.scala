import org.specs2._
import org.specs2.matcher.MatchResult

class AgentSpec extends Specification with StateExample { def is =            s2"""
  This is a specification to check Agent

  Utility function should
    evaluate initial state as 0.0,                                            $utility1
    evaluate GameOver as -1000.0,                                             $utility2
    evaluate an active state by lineCount                                     $utility3
    penalize having gaps between the columns                                  $utility4

  Solver should
    pick MoveLeft for s1                                                      $solver1
    pick Drop for s3                                                          $solver2

  Penalty function should
    penalize having blocks stacked up high                                    $penalty1
                                                                              """

  import com.eed3si9n.tetrix._
  import Stage._

  val agent = new Agent

  def utility1: MatchResult[Any] =
    agent.utility(s1) must_== 0.0
  def utility2: MatchResult[Any] =
    agent.utility(gameOverState) must_== -1000.0
  def utility3: MatchResult[Any] = {
    val s = Function.chain(Nil padTo (19, tick))(s3)
    agent.utility(s) must_== 1.0
  }
  def solver1: MatchResult[Any] =
    agent.bestMove(s1) must_== MoveLeft
  def solver2: MatchResult[Any] =
    agent.bestMove(s3) must_== Drop

  def utility4: MatchResult[Any] = {
    val s = newState(Seq(
      (0, 0), (0, 1), (0, 2), (0, 3), (0, 4), (0, 5), (0, 6))
      map { Block(_, TKind) }, (10, 20), TKind :: TKind :: Nil)
    agent.utility(s) must_== -36.0
  }

  def penalty1: MatchResult[Any] = {
    val s = newState(Seq(
      (0, 0), (0, 1), (0, 2), (0, 3), (0, 4), (0, 5), (0, 6))
      map { Block(_, TKind) }, (10, 20), TKind :: TKind :: Nil)
    agent.penalty(s) must_== 49.0
  } and {
    val s = newState(Seq((1, 0))
      map { Block(_, ZKind) }, (10, 20), TKind :: TKind :: Nil)
    agent.penalty(s) must_== 1.0
  }
}