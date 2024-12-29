package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import scala.collection.mutable.ListBuffer
import scala.math.hypot

class Grid(cells: Array[Array[Cell]]) {
  val height: Int = cells.length
  val width: Int  = if (height > 0) cells(0).length else 0
  val cellSize: Int = GameConfig.get.resolution.cellSize
  val players: ListBuffer[Player] = ListBuffer()
  private var fg : FunGraphics = new FunGraphics(width = cells(0).length * cellSize , height = cells.length * cellSize)

  def isWallAt(pos: Position): Boolean = {
    val ix: Int = pos.x / cellSize
    val iy: Int = pos.y / cellSize

    if (!inBounds(pos)) {
      true
    } else {
      cells(iy)(ix).terrain match {
        case Wall(_) => true
        case _       => false
      }
    }
  }

  def getFG(): FunGraphics = fg

  private def inBounds(position: Position): Boolean = {
    position.x >= 0 && position.x < width * cellSize && position.y >= 0 && position.y < height * cellSize
  }

  def update(): Unit = {
    for (player <- players) {
      for (tank <- player.tanks) {
        for (ammo <- tank.projectiles) {
          ammo.move()
          if(isWallAt(ammo.position)){
            println(s"Collision with wall at X:${ammo.position.x} Y:${ammo.position.y}")
            ammo.bounce()
            ammo.move()
          }
          //checkTankCollision(ammo)
        }
        tank.projectiles.filterInPlace(_.damage > 0)
      }
    }
    updateCells()
    drawGrid()
  }

  private def updateCells(): Unit = {
    players.foreach(p => {
      p.tanks.foreach(tank => {
        if (inBounds(tank.position)) {
          cells(tank.position.y)(tank.position.x).maybeTank = Some(tank) //TODO: Make the same as projectiles here
          tank.projectiles.foreach(a => {
            // Avoid outOfBounds
            val iX = if(a.position.x / cellSize >= cells.head.length) 74 else a.position.x / cellSize
            val iY = if(a.position.y / cellSize >= cells.length) 74 else a.position.y / cellSize
            cells(iY)(iX).maybeAmmo = Some(a)
          })
        }
      })
    })
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
          } else if(cell.maybeAmmo.isDefined){
            if(xPixel == cell.maybeAmmo.get.position.x &&
               yPixel == cell.maybeAmmo.get.position.y){
              fg.setColor(cell.maybeAmmo.get.projectileColor)
            } else {
              fg.setColor(cell.terrain.getColor)
            }
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
