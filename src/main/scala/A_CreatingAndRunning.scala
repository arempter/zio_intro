import zio.ZIO
import zio.console.{Console, putStrLn}
import zio._

object A_CreatingAndRunning {

  // effect definition
  //
  // ZIO[R, E, A]
  //  R - Environment Type
  //  E - Failure Type
  //  A - Success Type
  def firstMethod(int: Int): ZIO[Console, Nothing, Unit] =
    putStrLn(s"this is output with $int")

  // this will not run, it is just description
  // val program = List(firstMethod, SecondMethod)
  val res = firstMethod(444)

  def main(args: Array[String]): Unit = {
    // legacy or when you do not control your main function, exec a the edge
    val runtime = Runtime.default
    runtime.unsafeRun(firstMethod(44))
  }
}
