name := "netent-test"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3" withSources () withJavadoc ()
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
libraryDependencies += "co.fs2" %% "fs2-core" % "2.2.1" // For cats 2 and cats-effect 2

bloopExportJarClassifiers.in(Global) := Some(Set("sources"))

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
)
