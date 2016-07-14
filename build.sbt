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

lazy val `rtp-test-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-test-lib.git#master"))
lazy val `rtp-io-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-io-lib.git#master"))
lazy val `rtp-mongo-lib` = RootProject(uri("git://github.com/UKHomeOffice/rtp-mongo-lib.git#master"))

val root = (project in file("."))/*.settings(
  projectDependencies := {
    Seq(
      (projectID in `rtp-test-lib`).value.excludeAll(ExclusionRule(organization = "uk.gov.homeoffice")),
      (projectID in `rtp-io-lib`).value.excludeAll(ExclusionRule(organization = "uk.gov.homeoffice")),
      (projectID in `rtp-mongo-lib`).value.excludeAll(ExclusionRule(organization = "uk.gov.homeoffice"))
    )
  }
)*/.dependsOn(`rtp-mongo-lib`).dependsOn(`rtp-io-lib`).dependsOn(`rtp-test-lib`)