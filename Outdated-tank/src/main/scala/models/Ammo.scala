package isc.game.outdatedtank.models

import java.awt.Color

class Ammo(var position: Position,
            var angle: Int,
            val damage: Int,
            var size: Int,
            var bounceLeft: Int,
            var projectileColor: Color,
            var velocity: Int,
            val owner: Tank, // TODO: Remove it when ammo won't hit himself right after he shot
            var hasHitPlayer: Boolean = false) {
  private val id: String = java.util.UUID.randomUUID.toString
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

  def getId: String = id

  // Get Dx Dy based of the angle
  def getDx: Int = movement.getOrElse(angle, (2,0))._1
  def getDy: Int = movement.getOrElse(angle, (2,0))._2

  def move(): Unit = {
    position.x += getDx
    position.y += getDy
  }

  // Triggered once it hit on a wall. To make the bounce effect
  def bounce(hitType: String): Unit = {
    var (dx, dy) = movement.getOrElse(angle, (2, 0)) // by default, get angle 0

    // Go backward one time
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

    // Go forward one time
    position.x += dx
    position.y += dy

    bounceLeft -= 1
  }

  // Function that returns if the ammo needs to be removed
  def isDead: Boolean = {
    bounceLeft < 0 || hasHitPlayer
  }
}