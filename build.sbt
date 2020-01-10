ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal,
  "aliyun" at "http://maven.aliyun.com/nexus/content/groups/public/",
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  "twitter" at "https://maven.twttr.com",

)

name := "DianDiExample"

version := "0.1-SNAPSHOT"

organization := "net.itdiandi"

ThisBuild / scalaVersion := "2.11.12"



val hadoopVersion = "2.7.0"

val hadoopDependencies = Seq(
  "org.apache.hadoop" % "hadoop-client" % hadoopVersion ,
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion ,
  "com.hadoop.gplcompression" % "hadoop-lzo" % "0.4.20",
)

val sparkVersion = "2.0.2"

val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion ,
  "org.apache.spark" %% "spark-hive" % sparkVersion ,
)

val flinkVersion = "1.7.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-scala" % flinkVersion ,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion ,
  "org.apache.flink" % "flink-core" % flinkVersion ,
  "org.apache.flink" %% "flink-connector-kafka-0.9" % flinkVersion ,
  "org.apache.flink" %% "flink-table" % flinkVersion ,
  "org.apache.flink" %% "flink-jdbc" % flinkVersion  ,
  "org.apache.flink" % "flink-statebackend-rocksdb_2.12" % flinkVersion,


)



val mysqlJdbcVersion = "5.1.8"

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= hadoopDependencies,
    libraryDependencies ++= sparkDependencies,
    libraryDependencies ++= flinkDependencies ,

    libraryDependencies += "net.openhft" % "zero-allocation-hashing" % "0.8" ,
    libraryDependencies += "com.alibaba" % "fastjson" % "1.2.42" ,
    libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.8" ,
    libraryDependencies += "redis.clients" % "jedis" % "3.0.0",
//    libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.3" exclude("com.typesafe.play","play"),
//    libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.3",
    libraryDependencies += "com.maxmind.geoip2" % "geoip2" % "2.5.0",
    libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.1",
    libraryDependencies += "org.roaringbitmap" % "RoaringBitmap" % "0.8.6",
    libraryDependencies += "junit" % "junit" % "4.10"

  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
assembly / mainClass := Some("com.opera.adx.Job")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile/fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated



// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = false)
