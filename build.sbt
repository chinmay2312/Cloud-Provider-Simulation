name := "chinmay_gangal_project"

version := "0.1"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.cloudsimplus" % "cloudsim-plus" % "4.3.2",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test)