
name := "scala-logic"

version := "0.1-SNAPSHOT"

organization := "scala-logic"

scalaVersion := "2.9.2"

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-deprecation") 

//testOptions in Test += Tests.Argument("xonly")

seq(ScctPlugin.instrumentSettings : _*)

// specs2 library
libraryDependencies ++= Seq(
   "org.specs2" %% "specs2" % "1.12.2" % "test" cross CrossVersion.full,
   "junit" % "junit" % "4.7"
 )

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
                  "releases"  at "http://scala-tools.org/repo-releases",
                  "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
                  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

