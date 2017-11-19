// TODO: This line must exist for sbt v0.13, and must not exist for sbt v1.0
// If I want to cross-build, I'llhave to figure this out
ScriptedPlugin.scriptedSettings

scriptedLaunchOpts ++= Seq(
	"-Xmx1024M",
	"-Dplugin.version=" + version.value
)

scriptedBufferLog := false
