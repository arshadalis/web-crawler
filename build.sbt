ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "web-crawler"
  )


  val akkaVersion = "2.6.19"

  libraryDependencies ++= Seq(
    "org.jsoup" % "jsoup" % "1.15.1",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "org.scalatest" %% "scalatest" % "3.2.12"
  )
