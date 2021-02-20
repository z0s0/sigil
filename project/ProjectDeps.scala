import sbt._

object ProjectDeps {
  object versions {
    val akka = "2.6.10"
    val `akka-http` = "10.2.1"
    val logback = "1.2.3"

    val postgresql = "42.2.15"
    val `flyway-core` = "6.5.5"
    val slf4j = "1.7.30"
    val tapir = "0.17.12"
    val managementVersion = "1.0.8"
    val akkaPersistenceJDBC = "3.5.2"
    val doobie = "0.9.0"
    val scalactic = "3.2.0"
    val scalatest = "3.2.0"
    val scalacheck = "1.14.3"
    val `scalacheck-1-14` = "3.2.0.0"
    val scalamock = "5.0.0"

    val `scalacheck-shapeless_1.14` = "1.2.3"
    val cats = "2.1.1"
    val zio = "1.0.3"
    val interopCats = "2.2.0.1"
    val `zio-config` = "1.0.0-RC27"
    val `zio-logging` = "0.5.2"
    val http4s = "0.21.11"
    val testcontainers = "0.38.1"
  }

  val catsDeps = Seq("org.typelevel" %% "cats-core" % versions.cats)

  val testDeps = Seq(
    "org.scalactic" %% "scalactic" % versions.scalactic % Test,
    "org.scalatest" %% "scalatest" % versions.scalatest % Test,
    "org.scalacheck" %% "scalacheck" % versions.scalacheck % Test,
    "org.scalatestplus" %% "scalacheck-1-14" % versions.`scalacheck-1-14` % Test,
    "org.scalamock" %% "scalamock" % versions.scalamock % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14` % Test
  )

  val dbDeps = Seq(
    "org.postgresql" % "postgresql" % versions.postgresql,
    "org.flywaydb" % "flyway-core" % versions.`flyway-core`,
    "com.dimafeng" %% "testcontainers-scala-scalatest" % versions.testcontainers % Test,
    "com.dimafeng" %% "testcontainers-scala-postgresql" % versions.testcontainers % Test
  )

  val doobieDeps = Seq(
    "org.tpolecat" %% "doobie-core" % versions.doobie,
    "org.tpolecat" %% "doobie-hikari" % versions.doobie,
    "org.tpolecat" %% "doobie-postgres" % versions.doobie,
    "org.tpolecat" %% "doobie-scalatest" % versions.doobie % Test
  )

  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-stream" % versions.akka,
    "com.typesafe.akka" %% "akka-http" % versions.`akka-http`,
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka,
    "com.typesafe.akka" %% "akka-slf4j" % versions.akka,
    "com.typesafe.akka" %% "akka-http-testkit" % versions.`akka-http`,
    "com.typesafe.akka" %% "akka-slf4j" % versions.`akka`,
    "com.typesafe.akka" %% "akka-actor-typed" % versions.`akka`,
    "com.typesafe.akka" %% "akka-cluster-typed" % versions.`akka`,
    "com.typesafe.akka" %% "akka-cluster-sharding-typed" % versions.`akka`,
    "com.typesafe.akka" %% "akka-persistence-typed" % versions.`akka`,
    "com.typesafe.akka" %% "akka-serialization-jackson" % versions.`akka`,
    "com.typesafe.akka" %% "akka-persistence-query" % versions.`akka`,
    "com.typesafe.akka" %% "akka-cluster" % versions.`akka`,
    "com.typesafe.akka" %% "akka-pki" % versions.`akka`,
    "com.typesafe.akka" %% "akka-distributed-data" % versions.`akka`,
    "com.typesafe.akka" %% "akka-remote" % versions.`akka`,
    "com.typesafe.akka" %% "akka-coordination" % versions.`akka`,
    "com.typesafe.akka" %% "akka-cluster-tools" % versions.`akka`,
  )
  val logDeps = Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "org.slf4j" % "slf4j-api" % versions.slf4j
  )

  val tapirDeps = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-core" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-play" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % versions.tapir
  )

  val circeDeps = Seq("de.heikoseeberger" %% "akka-http-circe" % "1.31.0")
  val zioDeps = List(
    "dev.zio" %% "zio" % versions.zio,
    "dev.zio" %% "zio-macros" % versions.zio,
    "dev.zio" %% "zio-interop-cats" % versions.interopCats,
    "dev.zio" %% "zio-config" % versions.`zio-config`,
    "dev.zio" %% "zio-config-magnolia" % versions.`zio-config`,
    "dev.zio" %% "zio-config-typesafe" % versions.`zio-config`,
    "dev.zio" %% "zio-test" % versions.zio % Test,
    "dev.zio" %% "zio-test-sbt" % versions.zio % Test,
    "dev.zio" %% "zio-test-magnolia" % versions.zio % Test,
    "dev.zio" %% "zio-logging" % versions.`zio-logging`,
    "dev.zio" %% "zio-logging-slf4j" % versions.`zio-logging`
  )

  val deps = akkaDeps ++
    logDeps ++
    tapirDeps ++
    testDeps ++
    catsDeps ++
    circeDeps ++
    doobieDeps ++
    dbDeps ++
    zioDeps
}
