package models

import java.awt.Color

class Wall(var hp: Int, val color: Color = new Color(153,137,112)) extends CellInterface {
  def getColor: Color = color
}
