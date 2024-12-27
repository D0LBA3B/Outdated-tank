package models

import hevs.graphics.FunGraphics
import scala.collection.mutable.ListBuffer
import scala.math.hypot

class Grid(cells: Array[Array[Cell]]) {
  val height: Int = cells.length
  val width: Int  = if (height > 0) cells(0).length else 0
  val cellSize: Int = 10
  val players: ListBuffer[Player] = ListBuffer()
  private var fg : FunGraphics = new FunGraphics(width = cells(0).length * cellSize , height = cells.length * cellSize)

  def isWallAt(pos: Position): Boolean = {
    val ix = pos.x.toInt
    val iy = pos.y.toInt
    if (!inBounds(pos)) false
    else {
      cells(iy)(ix).terrain match {
        case Wall(_) => true
        case _       => false
      }
    }
  }

  def getFG(): FunGraphics = fg

  def addPlayer(p: Player): Unit = {
    if (!players.contains(p)) {
      players += p

      p.tanks.foreach(tank => {
        if (inBounds(tank.position)) {
          cells(tank.position.y.toInt)(tank.position.x.toInt).maybeTank = Some(tank)
        }
      })
    }
  }

  private def inBounds(position: Position): Boolean = {
    position.x >= 0 && position.x < width && position.y >= 0 && position.y < height
  }

  def update(): Unit = {
    for (player <- players) {
      for (tank <- player.tanks) {
        for (ammo <- tank.projectiles) {
          ammo.move()
          if (checkWallCollision(ammo)) {
            println("BOOOM WALLLL !")
            ammo.damage = 0
          }
          checkTankCollision(ammo)
        }
        tank.projectiles.filterInPlace(_.damage > 0)
      }
    }
}

  def drawGrid(): Unit = {
    for (rowIndex <- cells.indices) {
      val row = cells(rowIndex)
      for (colIndex <- row.indices) {
        val cell = row(colIndex)

        for (py <- 0 until cellSize; px <- 0 until cellSize) {
          fg.setColor(cell.getColor)

          val xPixel = colIndex * cellSize + px
          val yPixel = rowIndex * cellSize + py

          if (cell.maybeTank.isDefined) {
            if (drawTankShape(px, py, cellSize)) {
              fg.setColor(cell.maybeTank.get.color)
            } else {
              fg.setColor(cell.terrain.getColor)
            }
          } else {
            fg.setColor(cell.terrain.getColor)
          }
          fg.setPixel(xPixel, yPixel)
        }
      }
    }
  }

  private def drawTankShape(px: Int, py: Int, cellSize: Int): Boolean = {
    if (
        // Line py=1 : columns x=3..6
        (py == 1 && px >= 3 && px <= 6) ||
        // Line py=2 : columns x=2..7
        (py == 2 && px >= 2 && px <= 7) ||
        // Line py=3: columns x=2, x=4, x=6 (a hole in the middle for the turret)
        (py == 3 && (px == 2 || px == 4 || px == 6)) ||
        // Line py=4 : columns x=2..7
        (py == 4 && px >= 2 && px <= 7) ||
        // Line py=5 : columns x=3..6
        (py == 5 && px >= 3 && px <= 6)
    ) true else false
  }

  private def checkWallCollision(ammo: Ammo): Boolean = inBounds(ammo.position) && isWallAt(ammo.position)

  private def checkTankCollision(ammo: Ammo): Unit = {
    val radius = 0.5
    for (p <- players; t <- p.tanks) {
      val dist = hypot(t.position.x - ammo.position.x, t.position.y - ammo.position.y)
      if (dist < radius) {
        t.takeDamage(ammo.damage)
        ammo.damage = 0
      }
    }
  }
}
