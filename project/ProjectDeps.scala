import ProjectDeps.versions.circeVsn
import sbt._

object ProjectDeps {
  object versions {
    val logback = "1.2.3"

    val postgresql = "42.2.15"
    val `flyway-core` = "6.5.5"
    val slf4j = "1.7.30"
    val doobie = "1.0.0-RC1"
    val scalactic = "3.2.0"
    val scalatest = "3.2.0"
    val scalacheck = "1.14.3"
    val `scalacheck-1-14` = "3.2.0.0"
    val scalamock = "5.0.0"

    val `scalacheck-shapeless_1.14` = "1.2.3"
    val cats = "2.7.0"
    val http4s = "0.23.11"
    val testcontainers = "0.38.1"
    val pureConfigVersion = "0.17.1"
    val circeVsn = "0.14.1"
    val ceVsn = "3.3.11"
    val tapir = "1.0.0-M7"
  }

  val catsDeps = List(
    "org.typelevel" %% "cats-core" % versions.cats,
    "org.typelevel" %% "cats-effect" % versions.ceVsn,
    "org.typelevel" %% "cats-effect-std" % versions.ceVsn
  )

  val circeDeps = List(
    "io.circe" %% "circe-core" % versions.circeVsn,
    "io.circe" %% "circe-generic" % versions.circeVsn,
    "io.circe" %% "circe-parser" % versions.circeVsn,
    "org.gnieh" %% "diffson-circe" % "4.1.1"
  )
  val configDeps = List(
    "com.github.pureconfig" %% "pureconfig" % versions.pureConfigVersion
  )

  val testDeps = List(
    "org.scalactic" %% "scalactic" % versions.scalactic % Test,
    "org.scalatest" %% "scalatest" % versions.scalatest % Test,
    "org.scalacheck" %% "scalacheck" % versions.scalacheck % Test,
    "org.scalatestplus" %% "scalacheck-1-14" % versions.`scalacheck-1-14` % Test,
    "org.scalamock" %% "scalamock" % versions.scalamock % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14` % Test
  )

  val dbDeps = List(
    "org.postgresql" % "postgresql" % versions.postgresql,
    "org.flywaydb" % "flyway-core" % versions.`flyway-core`,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % versions.testcontainers % Test,
    "com.dimafeng" %% "testcontainers-scala-postgresql" % versions.testcontainers % Test
  )

  val doobieDeps = List(
    "org.tpolecat" %% "doobie-core" % versions.doobie,
    "org.tpolecat" %% "doobie-hikari" % versions.doobie,
    "org.tpolecat" %% "doobie-postgres" % versions.doobie,
    "org.tpolecat" %% "doobie-scalatest" % versions.doobie % Test
  )

  val logDeps = Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "org.slf4j" % "slf4j-api" % versions.slf4j
  )

  val http4sDeps = List(
    "org.http4s" %% "http4s-blaze-server" % versions.http4s,
    "org.http4s" %% "http4s-circe" % versions.http4s,
    "org.http4s" %% "http4s-dsl" % versions.http4s
  )

  val tapirDeps = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-core" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % versions.tapir
  )

  val deps = logDeps ++
    testDeps ++
    catsDeps ++
    tapirDeps ++
    doobieDeps ++
    circeDeps ++
    dbDeps ++
    configDeps ++
    http4sDeps
}
