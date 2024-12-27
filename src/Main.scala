import models.{Cell, Game, Grid, Player, Terrain}

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    println("We are ready for the war")
    MapReader.ReadJson("map1.json")

    val game :Game = new Game(
      name = "An epic battle",
      grid = new Grid(MapReader.ReadJson("map1.json"))
    )
    game.start()
  }
}