ThisBuild / version      := "4.2.1-M2"

ThisBuild / scalaVersion := "3.3.3"  // use LTS

ThisBuild / organization := "io.github.reqt"

console / initialCommands := """import reqt.*"""

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

lazy val nameOfThisBuild = "reqt-lang"

lazy val `reqt-lang` = (project in file("."))
  .settings(
    name := nameOfThisBuild,
    scalacOptions := List("-encoding", "utf8", "-Werror", "-deprecation", "-unchecked")
  )

lazy val meta = taskKey[Unit]("generate meta model")
meta := (Test / run).toTask("").value

lazy val build = taskKey[Unit]("build all the things")
build := Def.sequential(
        Compile / clean,
        meta,
        Compile / clean,
        Compile / compile,
        Compile / packageBin,
        Test / test,
      ).value

lazy val hello = taskKey[Unit]("Prints welcome message")

hello := println(s"""
  *** Welcome to the $nameOfThisBuild build in sbt ***
  
  meta       generate meta files, or use the underlying `Test / run` task
  test       run all tests
  package    build a jar in target/scala-x.y.z
  build      clean + all of the above
  console    enter the Scala REPL with reqt.* imported
  hello      see this message
""")

lazy val myStartupTransition: State => State = { s: State =>
  "hello" :: s
}

Global / onLoad := {
  // https://www.scala-sbt.org/1.0/docs/offline/Howto-Startup.html
  val old = (Global/onLoad).value
  myStartupTransition.compose(old)
}