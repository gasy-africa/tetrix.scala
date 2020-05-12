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