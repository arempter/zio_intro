import zio.console.putStrLn
import zio.duration._
import zio.{ExitCode, URIO, ZIO}

object F_Fibers extends zio.App {

  // fibers are lightweight and can be interrupted

  val job1 = putStrLn("Running job 1") *>
    ZIO.succeed("OK").delay(200.milliseconds) *>
    putStrLn("Job 1 complete")

  val job2 = putStrLn("Running job 2") *>
    ZIO.succeed("OK").delay(500.milliseconds) *>
    putStrLn("Job 2 complete")

  val program = for {
    f1 <- job1.fork
    f2 <- job2.fork
    _ <- f1.join
    //_ <- f2.join
    _ <- f2.interrupt
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
