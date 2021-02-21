package sigil

import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sigil.api.Layer.Routes
import sigil.config.ApiConfig
import zio.interop.catz.implicits.ioTimer
import zio.interop.catz._
import zio._

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program = for {
      routes <- ZIO.access[Routes](_.get.route)
      apiConf <- ZIO.access[Has[ApiConfig]](_.get)
      _ <- startHttp(routes, apiConf)
    } yield ()

    program.provideCustomLayer(DI.live).exitCode
  }

  private def startHttp[R](routes: HttpRoutes[zio.Task],
                           apiConfig: ApiConfig): ZIO[R, Throwable, Unit] = {
    import org.http4s.implicits._

    ZIO.runtime[R].flatMap { implicit rt =>
      val httpApp = Router("api" -> routes).orNotFound

      BlazeServerBuilder[zio.Task]
        .withHttpApp(httpApp)
        .bindHttp(apiConfig.port, "127.0.0.1")
        .serve
        .compile
        .drain
    }
  }
}
