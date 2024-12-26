import models.{Cell, Game, Terrain, Wall}

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    println("We are ready for the war")
    MapReader.ReadJson("map1.json")

    val game :Game = new Game(
      name = "An epic battle",
      grid = MapReader.ReadJson("map1.json")
    )

    game.drawGrid()
  }
}