name := "hazelbox"

organization := "net.godcode"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.0-1"

crossScalaVersions := Seq("2.9.0-1")

libraryDependencies := Seq(
  "com.hazelcast" % "hazelcast" % "1.9.4.2",
  "com.hazelcast" % "hazelcast-client" % "1.9.4.2",
  "com.mongodb.casbah" %% "casbah" % "2.1.5.0",
  "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.RC2" % "test"
)

scalacOptions += "-deprecation"

javaOptions += "-Djava.net.preferIPv4Stack=true"

resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

initialCommands := "import net.godcode.hazelbox._"
