package com.eed3si9n

package object tetrix {

  type State[A] = A => A

  type Sequences[A] = State[Seq[A]]

}
