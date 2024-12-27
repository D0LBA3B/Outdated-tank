ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.21",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.21",
  "com.typesafe.play" %% "play-json" % "2.10.6",
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3"
)

Compile / unmanagedClasspath += baseDirectory.value / "lib" / "fungraphics-1.5.15.jar"

lazy val root = (project in file("."))
  .settings(
    name := "OutdatedTank",
    idePackagePrefix := Some("ch.ISC.OutdatedTank")
  )
