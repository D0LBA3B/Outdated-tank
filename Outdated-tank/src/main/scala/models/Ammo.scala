package models

class Ammo(var position: Position,
            var angle: Int,
            var damage: Int = 10,
            var size: Int,
            val shotOn: Long,
            var rebounce: Int) {

  def move(): Unit = {
    angle match {
      case 0 =>
        position.x += 2
      case 30 =>
        position.x += 2
        position.y += 1
    }

    // TODO: Implement other angles
  }
}