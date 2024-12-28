package isc.game.outdatedtank.models

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
      case 45 =>
        position.x += 1
        position.y += 1
      case 60 =>
        position.x += 1
        position.y += 2
      case 90 =>
        position.y += 2
      case 120 =>
        position.x += -1
        position.y += 2
      case 135 =>
        position.x += -1
        position.y += 1
      case 150 =>
        position.x += -2
        position.y += 1
      case 180 =>
        position.x += -2
      case 210 =>
        position.x += -2
        position.y += -1
      case 225 =>
        position.x += -1
        position.y += -1
      case 240 =>
        position.x += -1
        position.y += -2
      case 270 =>
        position.y += -2
      case 300 =>
        position.x += 1
        position.y += -2
      case 315 =>
        position.x += 1
        position.y += -1
      case 330 =>
        position.x += 2
        position.y += -1
      case _ =>
        position.x += 2
    }
  }
}