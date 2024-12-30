package isc.game.outdatedtank.models

import hevs.graphics.{FunGraphics, ImageGraphics}
import hevs.graphics.utils.GraphicsBitmap
import isc.game.outdatedtank.MapReader

import java.awt.{Color, Desktop, Dimension, Image}
import java.io.{File, FileInputStream}
import java.net.URI
import javax.imageio.ImageIO

object Game {
  private var gameInstance: Game = null
  private var gameWindow: FunGraphics = null

  def getInstance(config: GameConfig): Game = {
    if (gameInstance == null) {
      gameInstance = new Game(config)
    }
    gameInstance
  }

  def getWindow(width: Int = 800, height: Int = 600, title: String = "Outdated Tank"): FunGraphics = {
    if (gameWindow == null) {
      gameWindow = new FunGraphics(width, height, title)
      val icon: Image = ImageIO.read(new File("./res/icon.png"))
      gameWindow.mainFrame.setIconImage(icon)
    }
    gameWindow
  }
}

class Game private(val config: GameConfig) {
  var isGameOver: Boolean = true

  def start(): Unit = {
    showMenu()
  }

  private def showMenu(): Unit = {
    val menuWidth = 750
    val menuHeight = 750
    val buttonWidth: Int = 200
    val buttonHeight: Int = 50
    val buttonSpacing: Int = 20

    val menuWindow = Game.getWindow(menuWidth, menuHeight)
    menuWindow.clear(new Color(140, 129, 107, 255))

    // Logo
    val bufferedLogo = ImageIO.read(new FileInputStream(new File("./res/logo.png")))
    val logoBitmap = new GraphicsBitmap("")
    logoBitmap.mBitmap = bufferedLogo
    val logoX = (menuWidth - bufferedLogo.getWidth) / 2
    val logoY = menuHeight / 4 - bufferedLogo.getHeight / 2
    menuWindow.drawPicture(logoX, logoY, logoBitmap)

    // Buttons
    val buttonLabels = List("FIGHT !", "OPTIONS", "CREDITS", "EXIT")
    val startY = logoY + bufferedLogo.getHeight  + 2 * buttonHeight
    buttonLabels.zipWithIndex.foreach { case (label, index) =>
      val buttonX: Int = (menuWidth - buttonWidth) / 2
      val buttonY: Int = startY + index * (buttonHeight + buttonSpacing)

      // background
      menuWindow.setColor(Color.LIGHT_GRAY)
      menuWindow.drawFillRect(buttonX, buttonY, buttonWidth, buttonHeight)

      // label
      menuWindow.setColor(Color.WHITE)
      val stringSize = menuWindow.getStringSize(label)
      val labelX = buttonX + (buttonWidth - stringSize.getWidth.toInt - 75) / 2
      val labelY = buttonY + (buttonHeight + stringSize.getHeight.toInt + 5) / 2
      menuWindow.drawString(labelX, labelY, label, Color.WHITE, 30)
    }

    // Menu events
    menuWindow.mainFrame.addMouseListener(new java.awt.event.MouseAdapter {
      override def mouseClicked(e: java.awt.event.MouseEvent): Unit = {
        val mouseX = e.getX
        val mouseY = e.getY

        buttonLabels.zipWithIndex.foreach { case (label, index) =>
          val buttonX: Int = (menuWidth - buttonWidth) / 2
          val buttonY: Int = startY + index * (buttonHeight + buttonSpacing)

          if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
            label match {
              case "FIGHT !" => launchGame()
              case "OPTIONS" => println("TODO")
              case "CREDITS" => Desktop.getDesktop.browse(new URI("https://github.com/D0LBA3B/Outdated-tank/"))
              case "EXIT" => System.exit(0)
              case _ =>
            }
          }
        }
      }
    })
  }

  private def launchGame(): Unit = {
    println("Game Started!")
    val grid: Grid = new Grid(MapReader.ReadJson(config.map.name))

    //TODO: Setup key listeners before while loop using config
    // config.players(X).controls.moveXX
    var tmpI = 1
    config.players.foreach(player => {
      var gamePlayer = new Player(player.name, player.color)
      gamePlayer.tanks.addOne(new Tank(position = new Position(tmpI, tmpI), color = player.color))
      grid.players.addOne(gamePlayer)
      tmpI += 5
    })

    grid.drawGrid()
    var count = 1
    while (isGameOver) {
      grid.update()
      if (count > 0){
        grid.players.head.tanks.head.fire(30)
        count += -1
      }
      Thread.sleep(10)
    }
  }
  //TODO: Setup live screen capture here instead
}