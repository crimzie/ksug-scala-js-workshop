name := "scalajs"
version := "0.0.1"

val http4sVersion = "0.18.1"
val scribeVersion = "2.2.0"

val root = crossProject.settings(
  scalaVersion := "2.12.4",
  version := "0.1",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.5.1",
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
    "io.monix" %%% "monix" % "3.0.0-M2",
    "com.outr" %%% "scribe" % scribeVersion,
  )
).jsSettings(
  name := "Client",
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.6.22"
).jvmSettings(
  name := "Server",
  scalacOptions += "-Ypartial-unification",
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "com.outr" %% "scribe-slf4j" % scribeVersion,
    "org.webjars" % "bootstrap" % "4.0.0-1",
  )
)

val rootJS = root.js
val rootJVM = root.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in (rootJS, Compile)).value
    (artifactPath in (rootJS, Compile, fastOptJS)).value
  }
)
