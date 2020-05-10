package com.eed3si9n.tetrix

object Stage {

  def newState(blocks: Seq[Block], gridSize: (Int, Int) = (10, 20), kinds: Seq[PieceKind]): GameState = {
    val size = gridSize
    val dummy = Piece((0, 0), TKind)
    val withNext = spawn(GameState(Nil, size, dummy, dummy, kinds, ActiveStatus)).
      copy(blocks = blocks)
    spawn(withNext)
  }

  val moveLeft: State[GameState] = transit { _.moveBy(-1.0, 0.0) }
  val moveRight: State[GameState] = transit { _.moveBy(1.0, 0.0) }
  val rotateCW: State[GameState] = transit { _.rotateBy(-math.Pi / 2.0) }
  val tick: State[GameState] = transit(_.moveBy(0.0, -1.0), Function.chain(clearFullRow :: spawn :: Nil) )
  val drop: State[GameState] = { s => Function.chain((Nil padTo (s.gridSize._2, transit {_.moveBy(0.0, -1.0)})) ++ List(tick))(s) }

  private[this] def transit(trans: Piece => Piece,
                              onFail: State[GameState] = identity): State[GameState] = { s =>
    s.status match {
      case ActiveStatus =>
        validate(s.copy(blocks = unload(s.currentPiece)(s.blocks), currentPiece = trans(s.currentPiece))) map { x =>
          x.copy(blocks = load(x.currentPiece)(x.blocks))
        } getOrElse { onFail(s) }
      case _ => s
    }
  }

  private[this] def validate: GameState => Option[GameState] = { s =>
    val (rowSize, colSize) = s.gridSize
    def inBounds(pos: (Int, Int)): Boolean = {
      val (row, col) = pos; (row >= 0) && (row < rowSize) && (col >= 0) && (col < colSize)
    }

    val currentPoss = s.currentPiece.current map {_.pos}
    if ((currentPoss forall inBounds) &&
      (s.blocks map {_.pos} intersect currentPoss).isEmpty) Some(s)
    else None
  }

  private[this] def unload(p: Piece): Sequences[Block] = { bs =>
    val currentPoss = p.current map {_.pos}
    bs filterNot { currentPoss contains _.pos  }
  }

  private[this] def load(p: Piece): Sequences[Block] = bs => bs ++ p.current

  private[this] lazy val spawn: State[GameState] = { (s: GameState) =>
      def dropOffPos: ( Double, Double) = {
        val (row, col) = s.gridSize; (row / 2.0, col - 2.0)
      }
      val s1 = s.copy(blocks = s.blocks,
        currentPiece = s.nextPiece.copy(pos = dropOffPos),
        nextPiece = Piece((2, 1), s.kinds.head),
        kinds = s.kinds.tail)
      validate(s1) map { x =>
        x.copy(blocks = load(x.currentPiece)(x.blocks))
      } getOrElse {
        s1.copy(blocks = load(s1.currentPiece)(s1.blocks), status = GameOver)
      }
    }

  private[this] lazy val clearFullRow: State[GameState] = { s0 =>
      def isFullRow(i: Int, s: GameState): Boolean =
        (s.blocks count {_.pos._2 == i}) == s.gridSize._1

      def tryRow(i: Int): State[GameState] = { s =>
        if (i < 0) s
        else if (isFullRow(i, s))
          tryRow(i - 1)(s.copy(blocks = (s.blocks filter {_.pos._2 < i}) ++
            (s.blocks filter {_.pos._2 > i} map { b =>
              b.copy(pos = (b.pos._1, b.pos._2 - 1)) })))
        else tryRow(i - 1)(s)
      }

      tryRow(s0.gridSize._2 - 1)(s0)
    }

}