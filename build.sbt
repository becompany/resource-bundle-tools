import Dependencies._

resolvers ++= Seq(
  "BeCompany Nexus" at "https://nexus.becompany.ch/content/repositories/public",
  sbtResolver.value
)

lazy val restClient = (
  Project("resource-bundle-tools", file("."))
  settings(
    organization := "ch.becompany",
    name := "resource-bundle-tools",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    libraryDependencies ++= dependencies
  )
)
