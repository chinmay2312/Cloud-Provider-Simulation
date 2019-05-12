name := "chinmay_gangal_project"

version := "0.1"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.cloudsimplus" % "cloudsim-plus" % "4.3.2",
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test)


enablePlugins(JavaAppPackaging, AshScriptPlugin)

mainClass in (Compile) := Some("com.uic.cs441.project.MainApp")

packageName in Docker := "regionalcloudsim"

version in Docker := "1.0.0"

dockerBaseImage := "moneyfarm/scala-sbt"

dockerRepository := Some("adarsh23")

dockerUpdateLatest := true
