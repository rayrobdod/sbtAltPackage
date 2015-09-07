package com.rayrobdod.sbtAltPackage

import sbt._
import Keys._

object Functions {
	
	/**
	 * Package files into a tar archive
	 */
	def tar(input:Seq[(File, String)], outputFile:File):Unit = {
		import java.io._
		import org.kamranzafar.jtar._
		
		val outputStream = new TarOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))
		
		input.filter{_._1.isFile}.map{x => ((x._1, new TarEntry(x._1, x._2)))}.foreach{x =>
			outputStream.putNextEntry(x._2)
			val origin = new BufferedInputStream(new FileInputStream( x._1 ))
			
			var count = 0
			var data = new Array[Byte](2048)
			while (-1 != count) {
				count = origin.read(data)
				if (count != -1) {
					outputStream.write(data, 0, count)
				}
			}
			
			outputStream.flush()
			origin.close()
		}
		
		outputStream.close()
	}
	
	/**
	 * Extract files into a tar archive.
	 * 
	 * used repeatedly in sbt-scripted-tests
	 */
	def untar(inputFile:File, outputDir:File):Unit = {
		import java.io._
		import org.kamranzafar.jtar._
		
		val inputStream = new TarInputStream(new BufferedInputStream(new FileInputStream(inputFile)))
		var entry:TarEntry = inputStream.getNextEntry();
		
		while(entry != null) {
			var count = 0;
			var data = new Array[Byte](2048);
			val name = entry.getName();
			(outputDir / name).getParentFile.mkdirs()
			
			val dest = new BufferedOutputStream(new FileOutputStream(outputDir / name));
			count = inputStream.read(data)
			while(count != -1) {
				dest.write(data, 0, count);
				count = inputStream.read(data);
			}
			
			dest.flush();
			dest.close();
			entry = inputStream.getNextEntry();
		}
	}
	
	/** Test whether the two files are equivalent.
	 * 
	 * If this returns normally, the file contents are equal;
	 * if this throws,then the file contents are not equal
	 * 
	 * used repeatedly in sbt-scripted-tests.
	 */
	def assertFileContentsEquals(exp:File, res:File):Unit = {
		if (! exp.exists()) {sys.error("source file does not exist")}
		if (! res.exists()) {sys.error("result file does not exist")}
		
		if (exp.length != res.length) {sys.error("source and result file have different contents")}
		
		val expBytes = sbt.IO.readBytes(exp)
		val resBytes = sbt.IO.readBytes(res)
		if (expBytes.toList != resBytes.toList) {sys.error("source and result file have different contents")}
		
		// else, OK
	}
	
}

object Plugin extends AutoPlugin {
	object autoImport {
		val pack200 = TaskKey[File]("pack200", "package a file using pack200")
		val targz = TaskKey[File]("targz", "package a set of files using a tarball")
	}
	import autoImport._
	override lazy val projectSettings = Seq(
		(pack200 in packageBin in Compile) := {
			import java.util.jar.Pack200.Packer
			
			val input = (packageBin in Compile).value
			val output = new File(input.toString + ".pack.gz")
			val options = Seq[(String,String)](
					(Packer.CLASS_ATTRIBUTE_PFX + "ScalaSig", "BBB"),
					(Packer.UNKNOWN_ATTRIBUTE, Packer.STRIP),
					// strip-debug
					(Packer.CODE_ATTRIBUTE_PFX + "LineNumberTable",    Packer.STRIP),
					(Packer.CODE_ATTRIBUTE_PFX + "LocalVariableTable", Packer.STRIP),
					(Packer.CLASS_ATTRIBUTE_PFX + "SourceFile",        Packer.STRIP)
			)
			sbt.Pack.pack(input, output, options)
			output
		},
		(targz in packageSrc in Compile) := {
			val inputs = (mappings in packageSrc in Compile).value
			val tarPath = new File(IO.split((artifactPath in packageSrc in Compile).value.toString)._1 + ".tar")
			val output = new File(tarPath.toString + ".gz")
			
			tarPath.getParentFile.mkdirs()
			Functions.tar(inputs, tarPath)
			sbt.IO.gzip(tarPath, output)
			output
		},
		(targz in packageDoc in Compile) := {
			val inputs = (mappings in packageDoc in Compile).value
			val tarPath = new File(IO.split((artifactPath in packageDoc in Compile).value.toString)._1 + ".tar")
			val output = new File(tarPath.toString + ".gz")
			
			tarPath.getParentFile.mkdirs()
			Functions.tar(inputs, tarPath)
			sbt.IO.gzip(tarPath, output)
			output
		}
	)
	
	override def requires = sbt.plugins.JvmPlugin
	override def trigger = allRequirements
}
