package isc.game.outdatedtank.models

import java.awt.Color

class Tank(var position: Position,
            var health: Int = 100,
            var color: Color = Color.RED) {

  val projectiles: collection.mutable.ListBuffer[Ammo] = collection.mutable.ListBuffer.empty

  def move(dx: Int, dy: Int): Unit = {
    position.x += dx
    position.y += dy
  }

  def fire(angle: Int): Unit = {
    val newAmmo = new Ammo(position=position.copy(),angle=angle,damage = 10,size = 1, shotOn=System.currentTimeMillis(), rebounce = 10, Color.black)
    projectiles += newAmmo
  }

  def takeDamage(dmg: Int): Unit = {
    health -= dmg
    if (health <= 0) {
      //TODO EXPLOSIONNNN
    }
  }
}