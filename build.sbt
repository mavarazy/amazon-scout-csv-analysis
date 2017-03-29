name := "aws"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.specs2" %% "specs2-core" % "3.8.9" % "test"
)
scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in assembly := Some("com.clemble.aws.analysis.Main")
assemblyJarName in assembly := "analyze.jar"