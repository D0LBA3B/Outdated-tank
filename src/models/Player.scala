package models

import java.awt.Color

class Player(val name: String, val color: Color) {
  val tanks: collection.mutable.ListBuffer[Tank] = collection.mutable.ListBuffer.empty

  def addTank(tank: Tank): Unit = tanks += tank
  def removeTank(tank: Tank): Unit = tanks -= tank
}