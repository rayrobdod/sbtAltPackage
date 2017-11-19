version := "0.1"

scalaVersion := "2.10.5"

artifactName in packageBin in Compile := {(a,b,c) => "binary.jar"}

artifactName in packageSrc in Compile := {(a,b,c) => "source.jar"}

artifactName in packageDoc in Compile := {(a,b,c) => "docume.jar"}

TaskKey[File]("untargz") in packageSrc in Compile := {
	val input = (targz in packageSrc in Compile).value
	val tarPath = new File(input.toString + ".ungzipped")
	val output = target.value / "untargz" / "src"
	sbt.IO.gunzip(input, tarPath)
	com.rayrobdod.sbtAltPackage.Functions.untar(tarPath, output)
	
	output
}

TaskKey[Unit]("verifyTar") in packageSrc in Compile := {
	import java.io._
	
	val expectedSeq = (mappings in packageSrc in Compile).value
	val resultDir = (TaskKey[File]("untargz") in packageSrc in Compile).value
	
	val expectedSeq2 = expectedSeq.map{x => ((x._1, resultDir / x._2))}
	expectedSeq2.foreach((com.rayrobdod.sbtAltPackage.Functions.assertFileContentsEquals _).tupled)
}
