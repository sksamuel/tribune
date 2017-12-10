lazy val root = Project("monkeytail", file("."))
  .settings(publish := {})
  .settings(publishArtifact := false)
  .settings(name := "monkeytail")
  .aggregate(
    core,
    macros
  )

lazy val macros = Project("monkeytail-macros", file("monkeytail-macros"))
  .settings(name := "monkeytail-macros")
  .settings(libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect"  % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "test"
  ))

lazy val core = Project("monkeytail-core", file("monkeytail-core"))
  .settings(name := "monkeytail-core")
  .dependsOn(macros)
