package isc.game.outdatedtank

import ScreenCapture.captureFrame
import models.{Game, GameConfig}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import scala.concurrent.duration.DurationInt

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("game-server")
    implicit val materializer: Materializer = Materializer(system)

    println("Hello world!")
    SoundPlayer.loadSounds()
    println("We are ready for the war")
    val game: Game = Game.getInstance(GameConfig.get)
    game.start()

    // Define HTTP routes
    val route =
      path("game") {
        get {
          val baos = new ByteArrayOutputStream()
          ImageIO.write(Game.getWindow().frontBuffer, "png", baos) // Capture the front buffer of FunGraphics as a PNG
          complete(HttpEntity(MediaTypes.`image/png`, baos.toByteArray))
        }
      } ~
        path("stream") {
          get {
            complete {
              akka.http.scaladsl.model.HttpEntity.Chunked.fromData(
                MediaTypes.`text/event-stream`,
                akka.stream.scaladsl.Source.tick(0.seconds, 100.millis, ()).map { _ =>
                  akka.util.ByteString(captureFrame())
                }
              )
            }
          }
        }

    // Start the HTTP server
    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8045)
    println("Server online at http://localhost:8045/game or /stream")
  }
}
