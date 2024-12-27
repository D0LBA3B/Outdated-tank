package models
import scala.math.{cos, sin, toRadians}

class Ammo(var position: Position,
            var angle: Double,
            var velocity: Double,
            var damage: Int = 10,
            var size: Int,
            val shotOn: Long,
            var rebounce: Int) {

  def move(): Unit = {
    val rad = toRadians(angle)
    position = position.copy(
      x = position.x + cos(rad) * velocity,
      y = position.y + sin(rad) * velocity)
  }
}