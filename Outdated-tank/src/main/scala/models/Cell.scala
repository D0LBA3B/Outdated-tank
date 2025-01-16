package isc.game.outdatedtank.models

import scala.collection.mutable.ListBuffer

class Cell(var terrain: Terrain, var maybeTank: Option[Tank] = None, var ammos: ListBuffer[Ammo] = new ListBuffer[Ammo]) {
  def getColor: java.awt.Color = {
    terrain.getColor
  }

  def updateTerrain(): Unit = {
    terrain match {
      case wall: Wall =>
        // Cast the terrain to Wall
        if (wall.hp <= 0) {
          terrain = OpenSpace // Change terrain to OpenSpace if hp is <= 0
        }
      case _ =>
    }
  }
}