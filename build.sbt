name := "bitcoin-arbitrage"
version := "1.0"
scalaVersion := "2.12.15"

lazy val commonDependencies = Seq(
  "com.lihaoyi" %% "requests" % "0.7.0",
  "io.circe" %% "circe-parser" % "0.14.2",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

lazy val app = (project in file("."))
  .settings(
    assembly / mainClass := Some("arbitrage.FindArbitrage"),
    libraryDependencies ++= commonDependencies
  )
