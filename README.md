# sbtAltPackage
Alternative archive formats for sbt package actions. Mostly tarballs.

[![Build Status](https://travis-ci.org/rayrobdod/sbtAltPackage.svg?branch=master)](https://travis-ci.org/rayrobdod/sbtAltPackage)

Adds three commands to sbt; namely:

<dl>
	<dt>compile:packageBin::pack200</dt><dd>Creates a pack200-conpressed version of the compiled files</dd>
	<dt>compile:packageSrc::targz  </dt><dd>Creates a tarball containing source files</dd>
	<dt>compile:packageDoc::targz  </dt><dd>Creates a tarball containing documentation files</dd>
</dl>

