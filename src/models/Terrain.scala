package models

import java.awt.Color

class Terrain(val color: Color = new Color(154,247,100))extends CellInterface{
  def getColor: Color = color
}
