import zio.clock.Clock
import zio.console.putStrLn
import zio.{ExitCode, Task, URIO, ZIO}
import zio.duration._

object C_ComposingEffects extends zio.App {

  val fastTask1: Task[Int] = {
    Task(2 + 2)
  }
  val slowerTask2: URIO[Clock, Int] = {
    ZIO.succeed(1 + 1)
  }.delay(200.microseconds)

  // compose by flatMap
  // dependency and errors are combined
  val program1: ZIO[Clock, Throwable, Int] = fastTask1 *> slowerTask2
  // or
  val program1a: ZIO[Clock, Throwable, Int] = fastTask1.flatMap(r => slowerTask2)

  // effects can be also mapped
  val program1b: ZIO[Clock, Throwable, URIO[Clock, Int]] = fastTask1.map(r => slowerTask2)

  // compose and run in parallel, return second result, either side will be interrupted in case of error
  val program2: ZIO[Clock, Throwable, Int] = fastTask1 &> slowerTask2

  // compose and run in parallel, return results combined, either side will be interrupted in case of error
  val program3: ZIO[Clock, Throwable, (Int, Int)] = fastTask1 <&> slowerTask2
  // or using zip
  val program3a: ZIO[Clock, Throwable, (Int, Int)] = fastTask1 zip slowerTask2

  // return faster, interrupt slower
  val program4 = program1 race program2

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = (
      for {
      r <- program3
      _ <- putStrLn(r._1 + r._2.toString)
      } yield ()
    ).exitCode

}
