libraryDependencies += {
	if (sbtVersion.value.charAt(0) == '0') {
		"org.scala-sbt" % "scripted-plugin" % sbtVersion.value
	} else {
		"org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
	}
}
