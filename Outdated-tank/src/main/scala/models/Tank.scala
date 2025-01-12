package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import isc.game.outdatedtank.SoundPlayer

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import java.net.URL
import javax.imageio.ImageIO

class Tank(var position: Position,
            var specificityConfig: SpecificityConfig) {

  private var health = specificityConfig.health
  private val fireCooldown: Long = specificityConfig.fireCooldown
  private val ammoDamage: Int = specificityConfig.ammoDamage
  private val ammoBounceLeft: Int = specificityConfig.ammoBounceLeft
  private var lastFireAt: Long = 0
  private var turretPosition: Int = 0

  var lastPosition: Position = null
  val projectiles: collection.mutable.ListBuffer[Ammo] = collection.mutable.ListBuffer.empty

  // tank design credits: https://zintoki.itch.io/ground-shaker
  val originalBody = ImageIO.read(
    new URL(s"https://raw.githubusercontent.com/D0LBA3B/Outdated-tank/develop/Outdated-tank/src/main/resources/${specificityConfig.name}/Bodies/body_tracks.png")
  )

  val originalTurret = ImageIO.read(
    new URL(s"https://raw.githubusercontent.com/D0LBA3B/Outdated-tank/develop/Outdated-tank/src/main/resources/${specificityConfig.name}/Weapons/turret_01_mk1.gif")
  )

  // resize images to 32x32
  val resizedBody = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
  val g2dBody: Graphics2D = resizedBody.createGraphics()
  g2dBody.drawImage(originalBody, 0, 0, 32, 32, null)
  g2dBody.dispose()
  val tankBodyBitmap = new GraphicsBitmap("")
  tankBodyBitmap.mBitmap = resizedBody

  val resizedTurret = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
  val g2dTurret = resizedTurret.createGraphics()
  g2dTurret.drawImage(originalTurret, 0, 0, 32, 32, null)
  g2dTurret.dispose()
  val tankTurretBitmap = new GraphicsBitmap("")
  tankTurretBitmap.mBitmap = resizedTurret

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
    if (currentIndex == -1) {
      turretPosition = validAngles.head
      return
    }

    val size = validAngles.size
    val newIndex =
      if (negativeDir)
        (currentIndex - 1 + size) % size
      else
        (currentIndex + 1) % size

    turretPosition = validAngles(newIndex)
  }

  def fire(): Unit = {
    if(System.currentTimeMillis() - fireCooldown >= lastFireAt) {
      SoundPlayer.playSound("shoot")
      val newAmmo = new Ammo(position=position.copy(), angle=turretPosition, damage = ammoDamage, size = 5, bounceLeft = ammoBounceLeft, projectileColor = Color.RED, owner = this, velocity = 3)
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

  def drawTank(fg: FunGraphics): Unit = {
    fg.drawPicture(position.x - 16, position.y - 16, tankBodyBitmap)

    val correctedAngle = turretPosition.toDouble + 90
    val rotatedTurret: BufferedImage = rotateImage(tankTurretBitmap.mBitmap, correctedAngle)
    val turretBitmapToDraw = new hevs.graphics.utils.GraphicsBitmap("")
    turretBitmapToDraw.mBitmap = rotatedTurret
    fg.drawPicture(position.x - rotatedTurret.getWidth / 2, position.y - rotatedTurret.getHeight / 2, turretBitmapToDraw)
  }

  // https://cloudinary.com/guides/image-effects/how-to-rotate-an-image-with-java
  private def rotateImage(inputImage: BufferedImage, angleDegrees: Double): BufferedImage = {
    val rotationAngle = Math.toRadians(angleDegrees)

    val width = inputImage.getWidth
    val height = inputImage.getHeight

    val newWidth  = (Math.abs(width  * Math.cos(rotationAngle)) + Math.abs(height * Math.sin(rotationAngle))).toInt
    val newHeight = (Math.abs(height * Math.cos(rotationAngle)) + Math.abs(width  * Math.sin(rotationAngle))).toInt

    val outputImage = new BufferedImage(newWidth, newHeight, inputImage.getType)

    val transform = new AffineTransform()
    transform.rotate(rotationAngle, newWidth / 2.0, newHeight / 2.0)
    transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0)

    val g2d: Graphics2D = outputImage.createGraphics()
    g2d.setTransform(transform)
    g2d.drawImage(inputImage, 0, 0, null)
    g2d.dispose()
    outputImage
  }

}