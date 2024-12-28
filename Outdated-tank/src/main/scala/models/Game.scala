package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import java.awt.Color

class Game(val name: String, val grid: Grid) {
  var isGameOver: Boolean = true

  def start(): Unit = {
    println("Game Started!")

    //TODO: Setup key listeners before while loop

    var michel = new Player("michel", Color.red)
    michel.tanks.addOne(new Tank(position = new Position(1, 1)))
    grid.addPlayer(michel) // TMP

    grid.drawGrid()
    while (isGameOver) {
      grid.update()
      Thread.sleep(50)
    }
  }

  def getFG(): FunGraphics = grid.getFG()


  //TODO: Setup live screen capture here instead
}