lazy val root = Project("monkeytail", file("."))
  .settings(name := "monkeytail")
  .settings(libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect"  % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "test"
  ))