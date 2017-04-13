name := "aws"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",

  "ch.qos.logback" % "logback-core" % "1.2.2",
  "ch.qos.logback" % "logback-classic" % "1.2.2",

  "org.apache.poi" % "poi" % "3.15",
  "org.apache.poi" % "poi-ooxml" % "3.15",

  "org.apache.lucene" % "lucene-analyzers-common" % "6.5.0",

  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.specs2" %% "specs2-core" % "3.8.9" % "test"
)
scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in assembly := Some("com.clemble.aws.analysis.Main")
assemblyJarName in assembly := "analyze.jar"