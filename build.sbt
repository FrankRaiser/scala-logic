
name := "scala-logic"

version := "0.1-SNAPSHOT"

organization := "scala-logic"

scalaVersion := "2.10.2"

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-feature",
  "-deprecation") 

//testOptions in Test += Tests.Argument("xonly")

seq(ScctPlugin.instrumentSettings : _*)

// specs2 library
libraryDependencies ++= Seq(
   "org.specs2" %% "specs2" % "2.1" % "test",
   "org.scala-lang" % "scala-reflect" % "2.10.1",  
   "junit" % "junit" % "4.7"
 )

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
                  "releases"  at "http://scala-tools.org/repo-releases",
                  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
                  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

