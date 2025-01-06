package isc.game.outdatedtank.models

class Cell(var terrain: Terrain, var maybeTank: Option[Tank] = None, var maybeAmmo: Option[Ammo] = None) {
  def getColor: java.awt.Color = {
    maybeTank match {
      case Some(tank) => tank.color
      case None => terrain.getColor
    }
  }
}