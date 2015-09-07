version := "0.1"

scalaVersion := "2.10.5"

artifactName in packageBin in Compile := {(a,b,c) => "binary.jar"}

artifactName in packageSrc in Compile := {(a,b,c) => "source.jar"}

artifactName in packageDoc in Compile := {(a,b,c) => "docume.jar"}

TaskKey[File]("untargz") in packageSrc in Compile := {
	import java.io._
	import org.kamranzafar.jtar._
	
	val input = (targz in packageSrc in Compile).value
	val tarPath = new File(input.toString + ".ungzipped")
	val output = target.value / "untargz" / "src"
	sbt.IO.gunzip(input, tarPath)
	
	val inputStream = new TarInputStream(new BufferedInputStream(new FileInputStream(tarPath)))
	var entry:TarEntry = inputStream.getNextEntry();
	
	while(entry != null) {
		var count = 0;
		var data = new Array[Byte](2048);
		val name = entry.getName();
		(output / name).getParentFile.mkdirs()
		
		val dest = new BufferedOutputStream(new FileOutputStream(output + "/" + name));
		count = inputStream.read(data)
		while(count != -1) {
			dest.write(data, 0, count);
			count = inputStream.read(data);
		}
		
		dest.flush();
		dest.close();
		entry = inputStream.getNextEntry();
	}
	output
}

TaskKey[Unit]("verifyTar") in packageSrc in Compile := {
	import java.io._
	
	val expectedSeq = (mappings in packageSrc in Compile).value
	val resultDir = (TaskKey[File]("untargz") in packageSrc in Compile).value
	
	val expectedSeq2 = expectedSeq.map{x => ((x._1, resultDir / x._2))}
	expectedSeq2.foreach({(exp:File, res:File) =>
		if (! exp.exists()) {error("source file does not exist")}
		if (! res.exists()) {error("result file does not exist")}
		
		if (exp.length != res.length) {error("source and result file have different contents")}
		
		val expBytes = sbt.IO.readBytes(exp)
		val resBytes = sbt.IO.readBytes(res)
		if (expBytes.toList != resBytes.toList) {error("source and result file have different contents")}
		
		// else, OK
	}.tupled)
}
