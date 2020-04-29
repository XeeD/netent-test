name := "netent-test"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.3" withSources () withJavadoc (),
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "co.fs2" %% "fs2-core" % "2.2.1",
  "io.chrisdavenport" %% "log4cats-core" % "1.0.1",
  "io.chrisdavenport" %% "log4cats-slf4j" % "1.0.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

bloopExportJarClassifiers.in(Global) := Some(Set("sources"))

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
)
