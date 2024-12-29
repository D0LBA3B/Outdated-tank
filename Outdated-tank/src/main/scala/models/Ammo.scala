package isc.game.outdatedtank.models

class Ammo(var position: Position,
            var angle: Int,
            var damage: Int = 10,
            var size: Int,
            val shotOn: Long,
            var rebounce: Int) {

  private val movement: Map[Int, (Int,Int)] = Map(
    0 -> (2, 0),
    30 -> (2, 1),
    45 -> (1, 1),
    60 -> (1, 2),
    90 -> (0, 2),
    120 -> (-1, 2),
    135 -> (-1, 1),
    150 -> (-2, 1),
    180 -> (-2, 0),
    210 -> (-2, -1),
    225 -> (-1, -1),
    240 -> (-1, -2),
    270 -> (0, -2),
    300 -> (1, -2),
    315 -> (1, -1),
    330 -> (2, -1)
  )

  def move(): Unit = {
   val (dx, dy) = movement.getOrElse(angle, (2, 0)) // by default, angle is 0
    position.x += dx
    position.y += dy
  }

  // Triggered once it hit on a wall. To make the bounce effect
  def bounce(): Unit = {
    println(s"Ammo had angle $angle°")

    // Know if it's horizontal / vertical / both hit ! TODO: FIND A WAY TO GET CELL SIZE replace to by the modulo
    val iX: Double = position.x % 10
    val iY: Double = position.y % 10
    var hitType: String = ""

    if(iX < iY) hitType = "vertical"
    if(iY < iX) hitType = "horizontal"
    if(iY == iX) hitType = "both"


    var (dx, dy) = movement.getOrElse(angle, (2, 0)) // by default, get angle 0

    // Go back to last position avoid ammo to be stuck in walls or map border
    position.x -= dx
    position.y -= dy

    // Adjust direction based of the hitType
    hitType match {
      case "both" =>
        // Change XY direction
        dx = -dx
        dy = -dy
      case "horizontal" =>
        // Change Y direction
        dy = -dy
      case "vertical" =>
        // Change X direction
        dx = -dx
    }

    //Reset the angle by the direction adjustment
    angle = movement.map(_.swap).getOrElse((dx,dy),0)

    println(s"Ammo have now angle $angle°")
    println(s"$rebounce left")

    rebounce -= 1
  }
}