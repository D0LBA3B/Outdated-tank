package isc.game.outdatedtank.models

import java.awt.Color

class Tank(var position: Position,
            var health: Int = 100,
            var color: Color = Color.RED,
            initialTurretPos: Int = 0) {

  var lastPosition: Position = null
  val projectiles: collection.mutable.ListBuffer[Ammo] = collection.mutable.ListBuffer.empty
  private[this] var turretPosition: Int = initialTurretPos

  private val validAngles: Seq[Int] = Seq(
    0, 30, 45, 60, 90, 120, 135, 150,
    180, 210, 225, 240, 270, 300, 315, 330
  )

  def move(dx: Int, dy: Int): Unit = {
    lastPosition = position.copy()
    position.x += dx
    position.y += dy
  }

  def moveTurret(negativeDir: Boolean = false): Unit = {
    val currentIndex = validAngles.indexOf(turretPosition)
    val newIndex = if(negativeDir) (currentIndex - 1) % validAngles.size else (currentIndex + 1) % validAngles.size
    turretPosition = validAngles(newIndex)
  }

  def fire(): Unit = {
    val newAmmo = new Ammo(position=position.copy(),angle=turretPosition,damage = 10,size = 1, shotOn=System.currentTimeMillis(), rebounce = 10, this.color)
    projectiles += newAmmo
  }

  def takeDamage(dmg: Int): Unit = {
    health -= dmg
    if (health <= 0) {
      //TODO EXPLOSIONNNN
    }
  }
}