package sigil

import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sigil.service.FlagService.FlagService
import zio.interop.catz.implicits.ioTimer
import zio.interop.catz._
import zio._

object ZMain extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program = for {
      _ <- UIO(println("hello"))
      repo <- ZIO.access[FlagService](_.get)
      list <- repo.list
      _ <- UIO(println(list))
    } yield ()

    program.provideCustomLayer(DI.live).exitCode
  }

  private def startHttp[R](
    routes: HttpRoutes[zio.Task]
  ): ZIO[R, Throwable, Unit] = {
    import org.http4s.implicits._

    ZIO.runtime[R].flatMap { implicit rt =>
      val httpApp = Router("api" -> routes).orNotFound

      BlazeServerBuilder[zio.Task]
        .withHttpApp(httpApp)
        .bindHttp(5100, "127.0.0.1")
        .serve
        .compile
        .drain
    }
  }
}
