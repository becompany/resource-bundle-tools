import sbt._

object Dependencies {
  
  val scalaIoVersion = "0.4.3"
  val commonsLangVersion = "2.6"
  val configVersion = "1.3.0"
  val spoiwoVersion = "1.0.6"
  val scoptVersion = "3.4.0"

  val scalaIoCore = "com.github.scala-incubator.io" %% "scala-io-core" % scalaIoVersion
  val scalaIoFile = "com.github.scala-incubator.io" %% "scala-io-file" % scalaIoVersion
  val commonsLang = "commons-lang" % "commons-lang" % commonsLangVersion
  val config = "com.typesafe" % "config" % configVersion
  val spoiwo = "com.norbitltd" % "spoiwo" % spoiwoVersion
  val scopt = "com.github.scopt" %% "scopt" % scoptVersion

  val dependencies = Seq(
    scalaIoCore,
    scalaIoFile,
    commonsLang,
    config,
    spoiwo,
    scopt
  )
}
