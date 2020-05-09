# Tetrix.scala

Learning project intended to follow Eugene Yokota's http://eed3si9n.com/tetrix-in-scala/ 13 day lesson

## :o: Install

1. I am using [sdkman](https://sdkman.io/) to install [Scala](https://www.scala-lang.org/) `2.13.x` 

```
% sdkman install scala 2.13.2
```

2. to create my projet I use `sbt new`

```
% sbt new scala/scala-seed.g8
[info] Loading settings for project global-plugins from idea.sbt ...
[info] Loading global plugins from /Users/valiha/.sbt/1.0/plugins
[info] Set current project to gasy-africa (in build file:/Users/valiha/Developer/gasy.africa/)
[info] Set current project to gasy-africa (in build file:/Users/valiha/Developer/gasy.africa/)

A minimal Scala project. 

name [Scala Seed Project]: tetrix.scala

Template applied in /Users/gasy/Developer/gasy.africa/./tetrix.scala
```

3. Intent

My goal here is to follow Eugene's teaching from start to finish with the current Scala library and versions. (2.13.2 as of May 2020)


## :a: Journey

#### Day :two:

on [Day2](http://eed3si9n.com/tetrix-in-scala/day2.html) I was looking for a fix of the `leftWall1` spec test but couldn't fix the issue with the day2 documentation. I started poking around and realized that Eugene must have not slept that night because the `Stage` class went from a class to a companion object and finally a State Monad.

I decided to take a partial copy of the [`Stage.scala`](https://github.com/eed3si9n/tetrix.scala/blob/day2/library/src/main/scala/main/com/tetrix/Stage.scala) file produced overnight, change the `Stage` class in `AbstractUI.scala` to the new state monad, added the `GameState` case class to the `pieces.scala` file then fixed the `StageSpec.scala`. I created a `Day2` tag based on the overnight event.

I then tried to implement the [rotation](http://eed3si9n.com/tetrix-in-scala/rotation.html) and realized that there was a [refactoring](http://eed3si9n.com/tetrix-in-scala/refactoring.html) explaining the State Monad fix. Well, I didn't want to go ahead of myself during in learning the game but may be I should have had.
