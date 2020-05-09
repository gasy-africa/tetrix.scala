package com.eed3si9n.tetrix

import scala.annotation.tailrec

object Stage {

  def newState(blocks: Seq[Block]): GameState = {
    val size = (10, 20)
    val dummy = Piece((0, 0), TKind)
    spawn(GameState(blocks, size, dummy))
  }

  val moveLeft: GameState => GameState = transit { _.moveBy(-1.0, 0.0) }
  val moveRight: GameState => GameState = transit { _.moveBy(1.0, 0.0) }
  val rotateCW: GameState => GameState = transit { _.rotateBy(-math.Pi / 2.0) }
  val tick: GameState => GameState = transit(_.moveBy(0.0, -1.0), Function.chain(clearFullRow :: spawn _ :: Nil) )

  private[this] def transit(trans: Piece => Piece,
                              onFail: GameState => GameState = identity): GameState => GameState =
    (s: GameState) => validate(s.copy(
      blocks = unload(s.currentPiece, s.blocks),
      currentPiece = trans(s.currentPiece))) map { x =>
      x.copy(blocks = load(x.currentPiece, x.blocks))
    } getOrElse {onFail(s)}

  private[this] def validate(s: GameState): Option[GameState] = {
    val size = s.gridSize
    def inBounds(pos: (Int, Int)): Boolean =
      (pos._1 >= 0) && (pos._1 < size._1) && (pos._2 >= 0) && (pos._2 < size._2)
    val currentPoss = s.currentPiece.current map {_.pos}
    if ((currentPoss forall inBounds) &&
      (s.blocks map {_.pos} intersect currentPoss).isEmpty) Some(s)
    else None
  }

  private[this] def unload(p: Piece, bs: Seq[Block]): Seq[Block] = {
    val currentPoss = p.current map {_.pos}
    bs filterNot { currentPoss contains _.pos  }
  }

  private[this] def load(p: Piece, bs: Seq[Block]): Seq[Block] =
    bs ++ p.current

  private[this] def spawn(s: GameState): GameState = {
    def dropOffPos: (Double, Double) = (s.gridSize._1 / 2.0, s.gridSize._2 - 3.0)
    val p = Piece(dropOffPos, TKind)
    s.copy(blocks = s.blocks ++ p.current,
      currentPiece = p)
  }

  private[this] lazy val clearFullRow: GameState => GameState =
    (s0: GameState) => {
      def isFullRow(i: Int, s: GameState): Boolean =
        (s.blocks count {_.pos._2 == i}) == s.gridSize._1
      @tailrec def tryRow(i: Int, s: GameState): GameState =
        if (i < 0) s
        else if (isFullRow(i, s))
          tryRow(i - 1, s.copy(blocks = (s.blocks filter {_.pos._2 < i}) ++
            (s.blocks filter {_.pos._2 > i} map { b =>
              b.copy(pos = (b.pos._1, b.pos._2 - 1)) })))
        else tryRow(i - 1, s)
      tryRow(s0.gridSize._2 - 1, s0)
    }

}