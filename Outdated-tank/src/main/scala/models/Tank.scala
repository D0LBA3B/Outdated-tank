package isc.game.outdatedtank.models

import java.awt.Color
import scala.collection.mutable.ListBuffer

class Tank(var position: Position,
            var health: Int = 100,
            var color: Color = Color.RED,
            initialTurretPos: Int = 0,
            fireCooldown: Long = 1500,
            ammoDamage: Int = 10,
           ammoBounceLeft: Int = 2) {

  var lastPosition: Position = null
  val projectiles: collection.mutable.ListBuffer[Ammo] = collection.mutable.ListBuffer.empty
  private[this] var turretPosition: Int = initialTurretPos
  private var lastFireAt: Long = 0
  private[this] val cooldown: Long = fireCooldown
  private[this] val damage: Int = ammoDamage

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
    val newIndex = if (negativeDir) (currentIndex - 1) % validAngles.size else (currentIndex + 1) % validAngles.size
    turretPosition = validAngles(newIndex)
  }

  def fire(): Unit = {
    if(System.currentTimeMillis() - cooldown >= lastFireAt) {
      val newAmmo = new Ammo(position=position.copy(), angle=turretPosition, damage = damage, size = 5, bounceLeft = ammoBounceLeft, projectileColor = color, owner = this)
      projectiles += newAmmo
      lastFireAt = System.currentTimeMillis()
    }
  }

  def removeProjectile(ammo :Ammo): Unit = {
    val index: Int = this.projectiles.indexWhere(_.getId == ammo.getId)
    if(index != -1) this.projectiles.remove(index)
  }

  def takeDamage(dmg: Int): Unit = {
    health -= dmg
    if (health <= 0) {
      //TODO EXPLOSIONNNN
    }
  }

  def getTankShape: ListBuffer[Position] = {
    val shape: ListBuffer[Position] = new ListBuffer[Position]

    for (x <- -2 to 2) { shape += Position(position.x + x,position.y + 4); shape += Position(position.x + x,position.y - 4)}
    for (x <- -3 to 3) { shape += Position(position.x + x,position.y + 3); shape += Position(position.x + x,position.y - 3)}
    for (x <- -4 to 4) { shape += Position(position.x + x,position.y + 2); shape += Position(position.x + x,position.y - 2)}
    for (x <- -6 to 6) { shape += Position(position.x + x,position.y + 1); shape += Position(position.x + x,position.y - 1)}
    for (x <- -6 to 6 by 2 if x != 0) { shape += Position(position.x + x,position.y)}
    shape
  }
}