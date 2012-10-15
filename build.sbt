
name := "scala-logic"

version := "0.1-SNAPSHOT"

organization := "scala-logic"

scalaVersion := "2.9.2"

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-deprecation") 

//testOptions in Test += Tests.Argument("xonly")

// specs2 library
libraryDependencies ++= Seq(
   "org.specs2" %% "specs2" % "1.12" % "test",
   "junit" % "junit" % "4.7"
 )

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
                  "releases"  at "http://scala-tools.org/repo-releases",
                  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

