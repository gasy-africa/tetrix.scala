import org.specs2._
import org.specs2.matcher.MatchResult

class AgentSpec extends Specification with StateExample { def is =            s2"""
  This is a specification to check Agent

  Utility function should
    evaluate initial state as 0.0,                                            $utility1
    evaluate GameOver as -1000.0,                                             $utility2
    evaluate an active state by lineCount                                     $utility3

  Solver should
    pick MoveLeft for s1                                                      $solver1
    pick Drop for s3                                                          $solver2
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
}