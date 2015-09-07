version := "0.1"

scalaVersion := "2.10.5"

artifactName in packageBin in Compile := {(a,b,c) => "binary.jar"}

artifactName in packageSrc in Compile := {(a,b,c) => "source.jar"}

artifactName in packageDoc in Compile := {(a,b,c) => "docume.jar"}

mainClass in Compile := Some("Main")

TaskKey[File]("unpack200") in packageBin in Compile := {
	val input = (pack200 in packageBin in Compile).value
	val output = new File(input.toString + ".unpack")
	sbt.Pack.unpack(input, output)
	output
}

TaskKey[Unit]("verify") := {
	val expected = "hello"
	val resultFile = (TaskKey[File]("unpack200") in packageBin in Compile).value
	
	val process = sbt.Process("java", Seq("-jar", resultFile.toString))
	val out = (process!!)
	if (out.trim != expected) {error("unexpected output: " + out)}
	()
}
