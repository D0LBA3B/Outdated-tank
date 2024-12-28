package models

import java.awt.Color

class Tank(var position: Position,
            var health: Int = 100,
            var color: Color = Color.RED) {

  val projectiles: collection.mutable.ListBuffer[Ammo] = collection.mutable.ListBuffer.empty

  def move(dx: Double, dy: Double): Unit = {
    position.x += dx
    position.y += dy
  }

  def fire(angle: Double): Unit = {
    val newAmmo = new Ammo(position.copy(), 0 , damage = 10, 10, System.currentTimeMillis(), 10)
    projectiles += newAmmo
  }

  def takeDamage(dmg: Int): Unit = {
    health -= dmg
    if (health <= 0) {
      //TODO EXPLOSIONNNN
    }
  }
}