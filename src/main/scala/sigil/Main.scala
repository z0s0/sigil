package sigil

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sigil.api.v1.{Docs, EvaluationRoutes, FlagRoutes, NamespaceRoutes}
import sigil.config.Config
import org.http4s.syntax.kleisli._
import sigil.util.Util
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Main {

  def main(args: Array[String]): Unit = {
    val list =
      List(Util.hash("pidr"), Util.hash("serega"), Util.hash("vovka"), Util.hash("vasiliy"))
    println(list)
    println(list.map(Util.toPositive))

    val server = for {
      config <- Config.load
      services <- Bootstrap.of(config)
      swaggerEndpoints = SwaggerInterpreter().fromEndpoints[IO](Docs.docs, "Sigil", "1.0")
      routes = new FlagRoutes(services.flagService).list ++ new NamespaceRoutes(
        services.namespaceService
      ).list ++ new EvaluationRoutes(services.evaluationService).list

      interpreter = Http4sServerInterpreter[IO]().toRoutes(routes ++ swaggerEndpoints)

      router = Router("/" -> interpreter).orNotFound
      _ <- BlazeServerBuilder[IO]
        .bindHttp(config.apiConfig.port)
        .withHttpApp(router)
        .resource
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield ()

    server.unsafeRunSync()
  }
}
