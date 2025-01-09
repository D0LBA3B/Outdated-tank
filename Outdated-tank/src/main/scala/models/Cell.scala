package isc.game.outdatedtank.models

import scala.collection.mutable.ListBuffer

class Cell(var terrain: Terrain, var maybeTank: Option[Tank] = None, var ammos: ListBuffer[Ammo] = new ListBuffer[Ammo]) {
  def getColor: java.awt.Color = {
    terrain.getColor
  }
}