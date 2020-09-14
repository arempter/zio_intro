import zio.{ExitCode, Schedule, Task, URIO, ZIO}

import scala.concurrent.Future
import scala.util.Random
import zio.duration._

object E_OperationsOnEffects extends zio.App {
  type Response = String

  def makeRequest(req: Int): Task[Response] = ZIO.fromFuture{ ec =>
    val rand = Random
    val r = rand.nextInt(5)
    r match {
      case r if r % 2 == 0 => Future.successful{
        println(s"Server response ok, req: $req")
        "OK"
      }
      case _ => Future.failed {
        println(s"Server failed, no response, req: $req")
        new Exception("No response")
      }
    }
  }
  val r1 = makeRequest(1)
  val r2 = makeRequest(2)
  val r3 = makeRequest(3)
  val requests = List(r1, r2, r3)
  val retryLogic = Schedule.exponential(600.milliseconds, 0.2) && Schedule.recurs(3) //&& Schedule.spaced(1.second)

  val program =
    ZIO.foreachParN(2)(requests){ resp =>
      resp.retry(retryLogic)
    }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.exitCode
}
