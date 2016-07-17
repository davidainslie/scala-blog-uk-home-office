name := "scala-blog-uk-home-office"

version := "1.0.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-Yrangepos",
  "-Yrepl-sync"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % "2.5.3" withSources(),
  "org.json4s" %% "json4s-jackson" % "3.2.11" withSources(),
  "com.squants" %% "squants" % "0.6.2" withSources()
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.8.4" % Test
)

val `rtp-test-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-test-lib.git#master"))
val `rtp-io-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-io-lib.git#master"))
val `rtp-mongo-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-mongo-lib.git#master"))
val `rtp-amazon-sqs-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-amazon-sqs-lib.git#master"))

val root = (project in file("."))
  .dependsOn(`rtp-test-lib` % "test->test;compile->compile")
  .dependsOn(`rtp-io-lib` % "test->test;compile->compile")
  .dependsOn(`rtp-mongo-lib` % "test->test;compile->compile")
  .dependsOn(`rtp-amazon-sqs-lib` % "test->test;compile->compile")