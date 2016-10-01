
name := "mycraft"

description := "Mycraft is a simple Minecraft clone written in Java, using LWJGL."

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

Seq(lwjglSettings: _*)

// Seems to be missing
//Seq(slickSettings: _*)

val sbtlwjglversion = "3.1.5"

libraryDependencies += "org.slick2d" % "slick2d-core" % "1.0.2"
