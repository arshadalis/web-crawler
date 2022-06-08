ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "web-crawler"
  )


val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.15.1",
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "org.scalatest" %% "scalatest" % "3.2.12"
)
