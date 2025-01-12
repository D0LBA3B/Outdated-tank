package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import scala.collection.mutable.ListBuffer
import scala.math.hypot

class Grid(cells: Array[Array[Cell]]) {
  val mapHeight: Int = cells.length
  val mapWidth: Int = cells.head.length
  val cellSize: Int = GameConfig.get.resolution.cellSize
  val players: ListBuffer[Player] = ListBuffer()
  val fg : FunGraphics = Game.getWindow(width = mapWidth , height = mapHeight);
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

  private def inBounds(position: Position): Boolean = position.x >= 0 && position.x < mapWidth * cellSize && position.y >= 0 && position.y < mapHeight * cellSize

  private def bounceType(a: Ammo): String = {
    // Make sur positions are natural number
    if (a.position.x < 0) a.position.x = 0
    if (a.position.y < 0) a.position.y = 0

    if(a.position.x == 0 || a.position.x >= mapHeight * cellSize - 1) "vertical"
    else if(a.position.y == 0 || a.position.y >= mapHeight * cellSize - 1) "horizontal"
    else if(cells(a.position.y / cellSize - 1)(a.position.x / cellSize).ammos.exists(_.getId == a.getId) ||
            cells(a.position.y / cellSize + 1)(a.position.x / cellSize).ammos.exists(_.getId == a.getId)){
      "horizontal"
    }
    else if (cells(a.position.y / cellSize)(a.position.x / cellSize - 1).ammos.exists(_.getId == a.getId) ||
             cells(a.position.y / cellSize)(a.position.x / cellSize + 1).ammos.exists(_.getId == a.getId)) {
      "vertical"
    }
    else {
      "both"
    }
  }

  private def hitTank(ammo: Ammo): Boolean = {
    players.foreach { player =>
      player.tanks.filter(_ != ammo.owner).foreach { tank =>
        // calculate the squared distance between the ammo and the tank center
        val dx = ammo.position.x - tank.position.x
        val dy = ammo.position.y - tank.position.y
        val dist2 = dx * dx + dy * dy

        val sumRadius = 16 + ammo.size // tank = 32×32 => 16
        // if the squared distance is within the squared sum of the radis, we have a collision
        if (dist2 <= sumRadius * sumRadius) {
          tank.takeDamage(ammo.damage)
          ammo.hasHitPlayer = true
          return true
        }
      }
    }
    false
  }

  def update(): Unit = {
    players.foreach(
      _.tanks.foreach(
        _.projectiles.foreach(
          ammo => {
            for(i <- 0 until ammo.velocity){
              updateCells()
              ammo.move()
              if(isWallAt(ammo.position)){
                ammo.bounce(bounceType(ammo))
              }
              hitTank(ammo)
            }
          }
        )
      )
    )
    updateCells()
    clearDeadObject()
    drawGrid()
  }

  private def updateCells(): Unit = {
    players.foreach(p => {
      p.tanks.foreach(tank => {
        if (inBounds(tank.position)) {

          // Tank updating..
          if(tank.lastPosition != null) {
            val oldCx = tank.lastPosition.x / cellSize
            val oldCy = tank.lastPosition.y / cellSize
            cells(oldCy)(oldCx).maybeTank = None
          }

          val cx = tank.position.x / cellSize
          val cy = tank.position.y / cellSize
          cells(cy)(cx).maybeTank = Some(tank)

          // Ammo updating..
          tank.projectiles.foreach(a => {
            //Remove it from last cell where she was
            cells.foreach {
              _.foreach {
                cell =>
                  if (cell.ammos.nonEmpty) {
                    val ammoIndex = cell.ammos.indexWhere(_.getId == a.getId)
                    if(ammoIndex != -1) cell.ammos.remove(ammoIndex)
                  }
              }
            }

            // Set the ammo in his new cell
            val iX2 = if (a.position.x / cellSize >= mapWidth) mapWidth - 1 else a.position.x / cellSize
            val iY2 = if (a.position.y / cellSize >= mapHeight) mapHeight - 1 else a.position.y / cellSize
            cells(iY2)(iX2).ammos += a
          })
        }
      })
    })
  }

  // Change Wall to OpenSpace if it's destroyed and remove dead ammo
  private def clearDeadObject(): Unit = {
    // Update terrain
    cells.foreach(
      _.foreach(
        _.updateTerrain()
      )
    )

    // Remove dead ammo from grid and tank
    players.foreach(p => {
      p.tanks.foreach(tank => {
        val ammoToRemove: ListBuffer[Ammo] = collection.mutable.ListBuffer.empty

        tank.projectiles.foreach(a => {
          if (a.isDead) {
            // Index calculation
            val iX = if (a.position.x / cellSize >= mapWidth) mapWidth - 1 else a.position.x / cellSize
            val iY = if (a.position.y / cellSize >= mapHeight) mapHeight - 1 else a.position.y / cellSize

            // Remove from grid
            val index = cells(iY)(iX).ammos.indexWhere(_.getId == a.getId)
            if(index != -1) cells(iY)(iX).ammos.remove(index)
            ammoToRemove.addOne(a)
          }
        })

        // Remove from tank
        ammoToRemove.foreach(a => tank.removeProjectile(a))
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
            tank.drawTank(fg)

            tank.projectiles.foreach(
              ammo => {
                fg.setColor(ammo.projectileColor)
                fg.drawFilledCircle(ammo.position.x, ammo.position.y, ammo.size)
              }
            )
          }
        ))
    }
  }
}
