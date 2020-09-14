import Database.{Database, select}
import zio.console.{Console, putStrLn}
import zio._


// modules are alternative to scala trait composition

case class User(name: String)

object Database {
  type Database = Has[Database.Service]

  trait Service {
    def select(id: Int): IO[String, User]
  }

  // implementation, can be different file etc.
  val live = ZLayer.succeed (
    new Service {
      override def select(id: Int): IO[String, User] = ZIO.succeed(User("ZioTest"))
    }
  )
  // accessor method, access DB module select method
  def select(id: Int): ZIO[Database, String, User] = ZIO.accessM(_.get.select(id))
}

object G_ModulesAndLayers extends zio.App {

  val env = Database.live ++ Console.live

  val program: ZIO[Database with Console, String, Unit] = for {
    u <- select(1)
    _ <- putStrLn("got user from db" + u.name)
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program.provideLayer(env).exitCode
}
