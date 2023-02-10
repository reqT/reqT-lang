ThisBuild / version      := "4.0.0"

ThisBuild / scalaVersion := "3.3.0-RC2"

ThisBuild / organization := "io.github.reqt"

console / initialCommands := """import reqt.*"""

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val `reqT-lang` = (project in file("."))
  .settings(
    name := "reqT-lang"
  )