import Dependencies.appDependencies

name := "marvel"

version := "1.0"

scalaVersion := "2.12.4"

mainClass in run := Some("com.brian.marvel.Application")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= appDependencies