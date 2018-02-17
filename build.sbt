name := "scalajs"

enablePlugins(WorkbenchPlugin)

val root = crossProject.settings(
  scalaVersion := "2.11.8",
  version := "0.1",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.3",
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.1"
  )
).jsSettings(
  name := "Client",
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
).jvmSettings(
  Revolver.settings:_*
).jvmSettings(
  name := "Server",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
    "com.typesafe.akka" %% "akka-actor" % "2.4.12",
    "org.webjars" % "bootstrap" % "3.2.0"
  )
)

val rootJS = root.js
val rootJVM = root.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in (rootJS, Compile)).value
    (artifactPath in (rootJS, Compile, fastOptJS)).value
  }
)
