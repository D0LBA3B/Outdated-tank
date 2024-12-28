package isc.game.outdatedtank.models

class Cell(var terrain: Terrain, var maybeTank: Option[Tank] = None) {
  def getColor: java.awt.Color = {
    maybeTank match {
      case Some(tank) => tank.color
      case None => terrain.getColor
    }
  }
}