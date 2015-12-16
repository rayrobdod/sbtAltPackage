sbtPlugin := true

name := "sbt-alt-package"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "1.0"

libraryDependencies += "org.kamranzafar" % "jtar" % "2.2"

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

licenses += (("The MIT License", new URL("http://opensource.org/licenses/MIT") ))
