import sbt._
import sbt.Keys._

object akka_examples extends Build {

  lazy val akka_examples = Project(
    id = "akka-examples",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "akka-examples",
      organization := "com.markusjais",
      version := "0.1-SNAPSHOT",
   	  scalaVersion := "2.10.2",
      resolvers ++= Seq(
            "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
      ),

      libraryDependencies ++= Seq(

        "com.typesafe.akka" %% "akka-actor" % "2.2.0" withSources (),
        "com.typesafe.akka" %% "akka-testkit" % "2.2.0" withSources ()
		
       )
    )
  )
}

