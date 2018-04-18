name := "faq"

version := "1.0"

scalaVersion := "2.12.1"



scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.jcenterRepo
)


libraryDependencies ++= {
  val akkaV       = "2.5.4"
  val akkaHttpV   = "10.0.1"
  val scalaTestV  = "3.0.0"
  lazy val circeVersion = "0.9.0"
  lazy val akkaJsonVersion = "1.20.0-RC1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaV,
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-persistence" % akkaV,
    "com.typesafe.akka" %% "akka-persistence-tck" % akkaV,
    "com.typesafe.akka" %% "akka-remote" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-persistence-query" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-caching" % "10.1.0",
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "org.typelevel" %% "cats-core" % "1.0.1",
    "io.circe" %% "circe-generic" % circeVersion,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "de.heikoseeberger" %% "akka-http-circe" % akkaJsonVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % "0.18",
    "org.apache.kafka"           % "kafka_2.12"           % "0.11.0.1",
    "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.23"
  )
}

scalacOptions += "-Ypartial-unification"