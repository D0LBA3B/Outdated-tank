package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import scala.collection.mutable.ListBuffer
import scala.math.hypot

class Grid(cells: Array[Array[Cell]]) {
  val height: Int = cells.length
  val width: Int  = if (height > 0) cells(0).length else 0
  val cellSize: Int = GameConfig.get.resolution.cellSize
  val players: ListBuffer[Player] = ListBuffer()
  val fg : FunGraphics = Game.getWindow(width = cells(0).length * cellSize , height = cells.length * cellSize); //new FunGraphics(width = cells(0).length * cellSize , height = cells.length * cellSize)
  fg.displayFPS(true)

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

  private def inBounds(position: Position): Boolean = position.x >= 0 && position.x < width * cellSize && position.y >= 0 && position.y < height * cellSize

  private def bounceType(a: Ammo): String = {
    // Make sur positions are natural number
    if (a.position.x < 0) a.position.x = 0
    if (a.position.y < 0) a.position.y = 0

    if(a.position.x == 0 || a.position.x >= cells.head.length * cellSize - 1) "vertical"
    else if(a.position.y == 0 || a.position.y >= cells.head.length * cellSize - 1) "horizontal"
    else if(cells(a.position.y / cellSize - 1)(a.position.x / cellSize).maybeAmmo.isDefined ||
            cells(a.position.y / cellSize + 1)(a.position.x / cellSize).maybeAmmo.isDefined){
      "horizontal"
    }
    else if (cells(a.position.y / cellSize)(a.position.x / cellSize - 1).maybeAmmo.isDefined ||
             cells(a.position.y / cellSize)(a.position.x / cellSize + 1).maybeAmmo.isDefined) {
      "vertical"
    }
    else {
      "both"
    }
  }

  def update(): Unit = {
    for (player <- players) {
      for (tank <- player.tanks) {
        for (ammo <- tank.projectiles) {
          ammo.move()
          if(isWallAt(ammo.position)){
            println(s"Collision with wall at X:${ammo.position.x} Y:${ammo.position.y}")
            ammo.bounce(bounceType(ammo))
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
          if(tank.lastPosition != null) {
            val oldCx = tank.lastPosition.x / cellSize
            val oldCy = tank.lastPosition.y / cellSize
            cells(oldCy)(oldCx).maybeTank = None
          }

          if(tank.health > 0) {
            val cx = tank.position.x / cellSize
            val cy = tank.position.y / cellSize
            cells(cy)(cx).maybeTank = Some(tank)
          }

          tank.projectiles.foreach(a => {
            //Remove it from last cell where she was
            var iX = if((a.position.x - a.getDx) / cellSize >= cells.head.length) cells.head.length - 1 else (a.position.x - a.getDx) / cellSize
            if(iX < 0) iX = 0
            var iY = if ((a.position.y - a.getDy) / cellSize >= cells.length) cells.length - 1 else (a.position.y - a.getDy) / cellSize
            if(iY < 0) iY = 0
            cells(iY)(iX).maybeAmmo = None

            // Set the ammo in this cell
            val iX2 = if(a.position.x / cellSize >= cells.head.length) 74 else a.position.x / cellSize
            val iY2 = if(a.position.y / cellSize >= cells.length) 74 else a.position.y / cellSize
            cells(iY2)(iX2).maybeAmmo = Some(a)
          })
        }
      })
    })
  }

  def drawGrid(): Unit = {
    fg.frontBuffer.synchronized {
      for (rowIndex <- cells.indices) {
        val row = cells(rowIndex)
        for (colIndex <- row.indices) {
          val cell = row(colIndex)

          for (py <- 0 until cellSize; px <- 0 until cellSize) {
            fg.setColor(cell.getColor)

            val xPixel = colIndex * cellSize + px
            val yPixel = rowIndex * cellSize + py

            fg.setPixel(xPixel,yPixel)
          }
        }
      }

      players.foreach(
        _.tanks.foreach(
          tank => {
            fg.setColor(tank.color)
            tank.getTankShape.foreach(
              position => {
                fg.setPixel(position.x,position.y)
              }
            )
            tank.projectiles.foreach(
              ammo => {
                fg.setColor(ammo.projectileColor)
                fg.drawFilledCircle(ammo.position.x, ammo.position.y, 5)
              }
            )
          }
        ))
    }
  }

  private def checkTankCollision(ammo: Ammo): Unit = {
    val radius = 0.5
    for (p <- players; t <- p.tanks) {
      val dist = hypot(t.position.x - ammo.position.x, t.position.y - ammo.position.y)
      if (dist < radius) {
        t.takeDamage(ammo.damage)
      }
    }
  }
}
