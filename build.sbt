enablePlugins(ScalaJSPlugin)

scalaVersion := "3.3.0"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "2.6.0"
)

scalaJSUseMainModuleInitializer := true

Compile / fullOptJS / artifactPath := baseDirectory.value / "index.js"
Compile / fastOptJS / artifactPath := baseDirectory.value / "index-fast.js"
