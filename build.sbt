name := "marvel"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.github.swagger-akka-http" % "swagger-akka-http_2.12" % "0.11.0",
  "com.github.tomakehurst" % "wiremock" % "2.6.0" % Test,
  "org.scalactic" %% "scalactic" % "3.0.4",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "de.heikoseeberger" % "akka-http-play-json_2.12" % "1.18.1",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % "test"
)
    