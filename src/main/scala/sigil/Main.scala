package sigil

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    IO(println("zdarova")).as(ExitCode.Success)
}
