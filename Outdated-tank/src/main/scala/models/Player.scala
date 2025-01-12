package isc.game.outdatedtank.models

class Player(val name: String) {
  val tanks: collection.mutable.ListBuffer[Tank] = collection.mutable.ListBuffer.empty

  def addTank(tank: Tank): Unit = tanks += tank
  def removeTank(tank: Tank): Unit = tanks -= tank
}