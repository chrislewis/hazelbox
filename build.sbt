name := "hazelbox"

organization := "net.godcode"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.8.1"

crossScalaVersions := Seq("2.8.1")

libraryDependencies := Seq(
  "net.databinder" %% "unfiltered-filter" % "0.5.0",
  "net.databinder" %% "unfiltered-jetty" % "0.5.0",
  "com.hazelcast" % "hazelcast" % "1.9.4.2",
  "com.hazelcast" % "hazelcast-client" % "1.9.4.2",
  "com.mongodb.casbah" %% "casbah" % "2.1.5.0",
  "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
)

scalacOptions += "-deprecation"

javaOptions += "-Djava.net.preferIPv4Stack=true"

resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

initialCommands := "import net.godcode.hazelbox._"
