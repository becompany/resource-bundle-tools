import Dependencies._

resolvers ++= Seq(
  "BeCompany Nexus" at "https://nexus.becompany.ch/content/repositories/public",
  sbtResolver.value
)

lazy val restClient = (
  Project("i18n", file("."))
  settings(
    organization := "ch.post",
    name := "pcc-i18n",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    libraryDependencies ++= dependencies
  )
)
