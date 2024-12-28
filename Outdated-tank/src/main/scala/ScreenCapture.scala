package isc.game.outdatedtank

import java.awt.{Rectangle, Robot, Toolkit}
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ScreenCapture {
  val robot = new Robot()
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  val captureArea = new Rectangle(screenSize)

  def captureFrame(): Array[Byte] = {
    val screenshot: BufferedImage = robot.createScreenCapture(captureArea)
    val baos = new ByteArrayOutputStream()
    ImageIO.write(screenshot, "png", baos)
    baos.toByteArray
  }
}
