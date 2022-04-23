package sigil

import zio._

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val program = for {
      _ <- UIO(println("zdarova"))
    } yield ()

    program.exitCode
  }

}
