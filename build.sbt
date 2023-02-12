ThisBuild / version      := "4.0.0"

ThisBuild / scalaVersion := "3.3.0-RC2"

ThisBuild / organization := "io.github.reqt"

console / initialCommands := """import reqt.*"""

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

lazy val `reqt-lang` = (project in file("."))
  .settings(
    name := "reqt-lang",
    scalacOptions := List("-encoding", "utf8", "-Werror", "-deprecation", "-unchecked")
  )

lazy val build = taskKey[Unit]("build all the things")
build := Def.sequential(
      (Test / run).toTask(""),
      (Test / test),
      Compile / packageBin,
    ).value

hello := println("""
  *** Welcome to the reqt-lang build in sbt ***

  type 'Test / run' to generate meta files 
  type 'test' to run all tests
  type 'package' to build jar in target/scala-x.y.z
  type 'build' to do all of the above
  type 'console' to enter the Scala REPL with reqt.* imported
  type 'hello' to see this message
""")

lazy val hello = taskKey[Unit]("Prints welcome message")

lazy val myStartupTransition: State => State = { s: State =>
  "hello" :: "scalaVersion" :: "version" :: "projects" :: s
}

Global / onLoad := {
  // https://www.scala-sbt.org/1.0/docs/offline/Howto-Startup.html
  val old = (Global/onLoad).value
  myStartupTransition.compose(old)
}