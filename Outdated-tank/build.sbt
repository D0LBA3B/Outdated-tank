ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.6",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.6",
  "com.typesafe.play" %% "play-json" % "2.10.6",
  "org.slf4j" % "slf4j-api" % "2.0.12",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0",
  "ch.qos.logback" % "logback-classic" % "1.5.6"
)

Compile / unmanagedClasspath += baseDirectory.value / "lib" / "fungraphics-1.5.15.jar"

lazy val root = (project in file("."))
  .settings(
    name := "OutdatedTank",
    idePackagePrefix := Some("ch.ISC.OutdatedTank")
  )
