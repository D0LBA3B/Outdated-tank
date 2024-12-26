package models

import hevs.graphics.FunGraphics

class Game(val name: String, private var grid: Array[Array[Cell]]){
  private var cellSize :Int = 50; // Define de size of length TODO: Remove it later it only helps for debbugging
  private var fg :FunGraphics = new FunGraphics(width = grid.length * cellSize, height = grid(0).length * cellSize)
  var isOver: Boolean = false

  def drawGrid(): Unit= {
    // Grid of cell exploration
    for(i <- grid.indices) {
      for ((c, j) <- grid(i).zipWithIndex) {

        //Draw square now 5x5
        for (y <- 0 until cellSize; x <- 0 until cellSize){
          fg.setColor(c.getColor)
          fg.setPixel(x + j * cellSize, y + i * cellSize)
        }
      }
    }
  }
}
