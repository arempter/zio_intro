import java.io.IOException

import zio.console._
import zio.{ExitCode, IO, Task, UIO, URIO, ZIO}

import scala.concurrent.Future

object B_CreateMoreEffects extends zio.App {

  // ZIO has some aliases
  //
  //  type IO[+E, +A]   = ZIO[Any, E, A]         // Succeed with an `A`, may fail with `E`        , no requirements.
  //  type Task[+A]     = ZIO[Any, Throwable, A] // Succeed with an `A`, may fail with `Throwable`, no requirements.
  //  type RIO[-R, +A]  = ZIO[R, Throwable, A]   // Succeed with an `A`, may fail with `Throwable`, requires an `R`.
  //  type UIO[+A]      = ZIO[Any, Nothing, A]   // Succeed with an `A`, cannot fail              , no requirements.
  //  type URIO[-R, +A] = ZIO[R, Nothing, A]     // Succeed with an `A`, cannot fail              , requires an `R`.

  // Creating effect with no side effects
  val stringEffect: UIO[String] = ZIO.succeed("OK")
  val intEffect: UIO[Int] = ZIO.succeed(44)
  // won't compile
//   val successfulTask: UIO[String] = ZIO.succeed(44)

  // for clarity you can create effectTotal
  val totalEffect: UIO[String] = ZIO.effectTotal("OK")

  // effect that fails
  val failEffect: IO[String, String] = ZIO.fail("NOOK")
  val exceptionEffect: Task[Int] = ZIO.fail(new IOException("bum"))

  // from scala code
  def okFuture: Future[String] = Future.successful("ok")

  // description
  val program1: ZIO[Console, Throwable, Unit] = for {
    _ <- putStrLn("run on: " + Thread.currentThread().getName)
    f <- ZIO.fromFuture { ec => okFuture }
    _ <- putStrLn(s"program1, got result from Future $f")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program1.exitCode

  val program2 = ZIO.fromFuture { ec => okFuture }

  // recover using fold
  //  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
  //    program2.foldM(f => ZIO.succeed(ExitCode.success), r => putStrLn(s"program2, got result from Future $r")).exitCode
}
