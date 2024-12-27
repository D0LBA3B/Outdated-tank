package models

import hevs.graphics.FunGraphics

import java.awt.Color

class Game(val name: String, val grid: Grid) {
  def start(): Unit = {
    println("Game Started!")
    var michel = new Player("michel", Color.red)
    michel.tanks.addOne(new Tank(position = new Position(1, 1)))
    grid.addPlayer(michel) // TMP

    grid.drawGrid()
    /*while (true) {
      grid.update()
      //TODO KEY EVENTS? - POSITIONS
      Thread.sleep(50)
    }*/
  }

  def getFG(): FunGraphics = grid.getFG()
}