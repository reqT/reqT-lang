lazy val reqTLangVer = "4.6.0"
lazy val scalaVer    = "3.3.5"  // use LTS only https://www.scala-lang.org/download/all.html
lazy val munitVer    = "1.0.3"
lazy val oslibVer    = "0.11.3"  // used as test dep only https://github.com/com-lihaoyi/os-lib/releases
lazy val scalaXmlVer = "2.2.0"   // deprecated; migration needed eventually...

ThisBuild / version      := reqTLangVer
ThisBuild / scalaVersion := scalaVer  // use LTS only
ThisBuild / organization := "io.github.reqt"

sourcesInBase := false

console / initialCommands := """import reqt.*"""

Global / onChangedBuildSource := ReloadOnSourceChanges

libraryDependencies += "org.scalameta" %% "munit" % munitVer % Test
libraryDependencies += "com.lihaoyi" %% "os-lib" % oslibVer % Test
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % scalaXmlVer

lazy val nameOfThisBuild = "reqt-lang"

lazy val `reqt-lang` = (project in file("."))
  .settings(
    name := nameOfThisBuild,
    scalacOptions := List("-encoding", "utf8", "-Werror", "-deprecation", "-unchecked", "-feature")
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