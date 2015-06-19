package com.rayrobdod.sbtAltPackage

import sbt._
import Keys._

object Functions {
	def pack200(input:File, output:File, options:Seq[String]):Unit = {
		val p = java.lang.Runtime.getRuntime.exec(
				("pack200" +: options :+ output.toString :+ input.toString).toArray[String]
		)
		p.waitFor()
	}
	
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
	
}

object Plugin extends AutoPlugin {
	object autoImport {
		val pack200 = TaskKey[File]("pack200", "package a file using pack200")
		val targz = TaskKey[File]("targz", "package a set of files using a tarball")
	}
	import autoImport._
	override lazy val projectSettings = Seq(
		(pack200 in packageBin in Compile) := {
			val input = (packageBin in Compile).value
			val output = new File(input.toString + ".pack.gz")
			val options = Seq[String]("-CScalaSig=BBB", "-G", "-Ustrip")
			Functions.pack200(input, output, options)
			output
		},
		(targz in packageSrc in Compile) := {
			val inputs = (mappings in packageSrc in Compile).value
			val tarPath = new File(IO.split((artifactPath in packageSrc in Compile).value.toString)._1 + ".tar")
			val output = new File(tarPath.toString + ".gz")
			
			Functions.tar(inputs, tarPath)
			sbt.IO.gzip(tarPath, output)
			output
		},
		(targz in packageDoc in Compile) := {
			val inputs = (mappings in packageDoc in Compile).value
			val tarPath = new File(IO.split((artifactPath in packageDoc in Compile).value.toString)._1 + ".tar")
			val output = new File(tarPath.toString + ".gz")
			
			Functions.tar(inputs, tarPath)
			sbt.IO.gzip(tarPath, output)
			output
		}
	)
	
	override def requires = sbt.plugins.JvmPlugin
	override def trigger = allRequirements
}
