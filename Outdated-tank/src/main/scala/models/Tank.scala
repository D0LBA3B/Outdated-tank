package isc.game.outdatedtank.models

import java.awt.Color
import scala.collection.mutable.ListBuffer

class Tank(var position: Position,
            var health: Int = 100,
            var color: Color = Color.RED,
            initialTurretPos: Int = 0) {

  var lastPosition: Position = null
  val projectiles: collection.mutable.ListBuffer[Ammo] = collection.mutable.ListBuffer.empty
  private[this] var turretPosition: Int = initialTurretPos
  private var lastFireAt: Long = 0
  private val fireCooldown: Long = s

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
    if(System.currentTimeMillis() - fireCooldown >= lastFireAt) {
      val newAmmo = new Ammo(position=position.copy(),angle=turretPosition,damage = 10,size = 1, shotOn=System.currentTimeMillis(), rebounce = 10, this.color)
      projectiles += newAmmo
      lastFireAt = System.currentTimeMillis()
    }
  }

  def takeDamage(dmg: Int): Unit = {
    health -= dmg
    if (health <= 0) {
      //TODO EXPLOSIONNNN
    }
  }

  def getTankShape: ListBuffer[Position] = {
    val shape: ListBuffer[Position] = new ListBuffer[Position]
    for (x <- 5 to 10) { shape += Position(position.x + x,position.y + 1); shape += Position(position.x + x,position.y + 9)}
    for (x <- 4 to 11) { shape += Position(position.x + x,position.y + 2); shape += Position(position.x + x,position.y + 8)}
    for (x <- 3 to 12) { shape += Position(position.x + x,position.y + 3); shape += Position(position.x + x,position.y + 7)}
    for (x <- 2 to 14) { shape += Position(position.x + x,position.y + 4); shape += Position(position.x + x,position.y + 6)}
    for (x <- 2 to 12 by 2) { shape += Position(position.x + x,position.y + 5)}
    shape
  }
}