import sbt.{ModuleID, _}

object Dependencies {

  private object Versions {
    val akka: String = "2.5.4"
  }

  val appDependencies =  Seq(
    "co.pragmati" %% "swagger-ui-akka-http" % "1.1.0",
    "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "1.1.2",
    "com.github.swagger-akka-http" % "swagger-akka-http_2.12" % "0.11.0",
    "com.typesafe.akka" %% "akka-http" % "10.0.10",
    "com.typesafe.akka" %% "akka-stream" % Versions.akka,
    "com.typesafe.akka" %% "akka-actor" % Versions.akka,
    "de.heikoseeberger" %% "akka-http-play-json" % "1.31.0",
    "org.typelevel" % "cats-core_2.12" % "0.9.0",
    "io.github.nafg" %% "slick-migration-api" % "0.7.0"
  )
}
