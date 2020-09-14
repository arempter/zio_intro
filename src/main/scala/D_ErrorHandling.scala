import java.io.IOException

import zio.console.putStrLn
import zio.{Cause, ExitCode, IO, Task, URIO, ZIO}

object D_ErrorHandling extends zio.App {

  val task1: Task[String] = ZIO.fail(new Exception("bum1"))
  val task2: Task[String] = ZIO.fail(new IOException("bum2"))
  val task3: Task[String] = ZIO.fail(new Exception("bum3"))

  val program = task1 &> task2 &> task3

  // run some orElse cleanup
  val p2 = program.orElse(ZIO.succeed("resources cleanup"))

  // since tasks run im parallel, failures list may differ
  val p3 = program.catchSomeCause {
    case c: Cause[Exception] =>
      putStrLn(c.failures.toString()) *>
      ZIO.succeed(ExitCode.success)
  }

  //  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
  //    program.catchAll(_ =>
  //       on Error do some cleanup or recovery
  //      putStrLn("We do not care")).exitCode

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = p3.exitCode
}
