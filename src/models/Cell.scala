package models

import java.awt.Color

trait CellInterface {
  def getColor: Color
}

class Cell(var cellType: CellInterface) {
  def getColor: Color = cellType.getColor
}
