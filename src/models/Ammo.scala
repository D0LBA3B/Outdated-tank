package models

import java.util.Date

class Ammo(
            private val damage: Int,
            private var rebounce: Int,
            private var posX: Int,
            private var posY: Int,
            private var angle: Int,
            private var size: Int,
            private val shotOn: Date
          ) {

  def hasHitWall(): Unit = {
    // Reduce rebounce

    // Change angle
  }

  def hasHitPlayer(): Unit = {
    // Get player and reduce his HP
    
  }
}
