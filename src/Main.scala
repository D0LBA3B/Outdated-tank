import models.{Cell, Game, Terrain, Wall}

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    println("We are ready for the war")

    val game :Game = new Game(
      name = "An epic battle",
      grid = Array(
          Array(new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Terrain())),
          Array(new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Wall(1)),new Cell(new Terrain()),new Cell(new Terrain())),
          Array(new Cell(new Terrain()),new Cell(new Wall(1)),new Cell(new Wall(1)),new Cell(new Wall(1)),new Cell(new Terrain())),
          Array(new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Wall(1)),new Cell(new Terrain()),new Cell(new Terrain())),
          Array(new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Terrain()),new Cell(new Terrain())),
      )
    )

    game.drawGrid()
  }
}