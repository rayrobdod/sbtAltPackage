sbtPlugin := true

name := "sbt-alt-package"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "a.0-SNAPSHOT"

libraryDependencies += "org.kamranzafar" % "jtar" % "2.2"

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
