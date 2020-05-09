import Dependencies._

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.2.0-SNAPSHOT"
ThisBuild / organization     := "com.eed3si9n"
ThisBuild / organizationName := "http://eed3si9n.com"

lazy val root = (project in file("."))
  .settings(
    name := "tetrix.scala",
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.1.1"

// Read here for optional jars and dependencies
libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "4.8.3" % "test")

scalacOptions in Test ++= Seq("-Yrangepos")
