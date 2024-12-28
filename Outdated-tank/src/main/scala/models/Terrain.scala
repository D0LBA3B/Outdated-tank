package isc.game.outdatedtank.models

import java.awt.Color

sealed trait Terrain {
  def getColor: Color
}

final case class Wall(hp: Int) extends Terrain {
  override def getColor: Color = Color.GRAY
}

case object OpenSpace extends Terrain {
  override def getColor: Color = new Color(154, 247, 100)
}