package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import isc.game.outdatedtank.{MapReader, SoundPlayer}

import java.awt.event.{KeyEvent, KeyListener}
import java.awt.{Color, Desktop, Font, Image}
import java.net.{URI, URL}
import javax.imageio.ImageIO
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

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
      val icon: Image = ImageIO.read(new URL("https://raw.githubusercontent.com/D0LBA3B/Outdated-tank/develop/Outdated-tank/res/icon.png"))
      gameWindow.mainFrame.setIconImage(icon)
    }
    gameWindow
  }
}

class Game private(val config: GameConfig) {
  var isGameInProgress: Boolean = false
  var isMenuActive: Boolean = true

  def start(): Unit = {
    showMenu()
  }

  private def showMenu(): Unit = {
    isMenuActive = true
    val menuWidth = 750
    val menuHeight = 750
    val buttonWidth: Int = 200
    val buttonHeight: Int = 50
    val buttonSpacing: Int = 20
    val fontSize: Int = 30
    val menuWindow = Game.getWindow(menuWidth, menuHeight)
    menuWindow.clear(new Color(140, 129, 107, 255))
    menuWindow.mainFrame.getKeyListeners.foreach(k => menuWindow.mainFrame.removeKeyListener(k))

    // Logo
    val bufferedLogo = ImageIO.read(new URL("https://raw.githubusercontent.com/D0LBA3B/Outdated-tank/develop/Outdated-tank/res/logo.png"))

    val logoBitmap = new GraphicsBitmap("")
    logoBitmap.mBitmap = bufferedLogo
    val logoX = (menuWidth - bufferedLogo.getWidth) / 2
    val logoY = menuHeight / 4 - bufferedLogo.getHeight / 2
    menuWindow.drawPicture(logoX, logoY, logoBitmap)

    // Buttons
    val buttonLabels = List("FIGHT \u2694", "OPTIONS", "CREDITS", "DONATE \u2764", "EXIT")
    val startY = logoY + bufferedLogo.getHeight  + 2 * buttonHeight
    buttonLabels.zipWithIndex.foreach { case (label, index) =>
      val buttonX: Int = (menuWidth - buttonWidth) / 2
      val buttonY: Int = startY + index * (buttonHeight + buttonSpacing)

      // background
      menuWindow.setColor(Color.LIGHT_GRAY)
      menuWindow.drawFillRect(buttonX, buttonY, buttonWidth, buttonHeight)

      // label
      menuWindow.setColor(Color.WHITE)
      val font = new Font("Segoe UI Emoji", Font.PLAIN, fontSize)
      val metrics = menuWindow.mainFrame.getFontMetrics(font)
      val labelX = buttonX + (buttonWidth - metrics.stringWidth(label)) / 2
      val labelY = buttonY + (buttonHeight + metrics.getHeight) / 2 - (metrics.getHeight - metrics.getAscent) / 2

      menuWindow.drawString(posX = labelX, posY = labelY, str = label, color = Color.WHITE, fontSize = 30, fontFamily =  "Segoe UI Emoji")
    }

    // Menu events
    var lastClicked = System.currentTimeMillis()
    menuWindow.mainFrame.addMouseListener(new java.awt.event.MouseAdapter {
      override def mouseClicked(e: java.awt.event.MouseEvent): Unit = {
        val insets = menuWindow.mainFrame.getInsets
        val mouseX = e.getX - insets.left
        val mouseY = e.getY - insets.top

        buttonLabels.zipWithIndex.foreach { case (label, index) =>
          val buttonX: Int = (menuWidth - buttonWidth) / 2
          val buttonY: Int = startY + index * (buttonHeight + buttonSpacing)

          if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
            label match {
              case "FIGHT \u2694" => {
                menuWindow.mainFrame.removeMouseListener(this)
                isMenuActive = false
                launchGame()
              }
              case "OPTIONS" => println("TODO")
              case "CREDITS" => Desktop.getDesktop.browse(new URI("https://github.com/D0LBA3B/Outdated-tank/"))
              case "DONATE \u2764" => Desktop.getDesktop.browse(new URI("https://buymeacoffee.com/dolba3b"))
              case "EXIT" => System.exit(0)
              case _ =>
            }
          }

          // Sound skip
          // mini-cooldown to prevent spamming and clip buggggg
          if(System.currentTimeMillis() - lastClicked > 200) {
            lastClicked = System.currentTimeMillis()
            if (mouseX >= 30 && mouseX <= 30 + 60 &&
              mouseY >= menuHeight - 50 && mouseY <= menuHeight - 50 + 30) {
              // TODO find a way to clear correctly this string
              DisplayCurrentSound(menuWindow, 30, menuHeight - 50, 60, 20, new Color(140, 129, 107, 255))
              SoundPlayer.skipMenuClip
            }
          }
        }
      }
    })

    SoundPlayer.playSound("menu", true)
    new Thread(() => {
      while(isMenuActive) {
        DisplayCurrentSound(menuWindow, 30, menuHeight - 50, 60, 30)
        Thread.sleep(500)
      }
    }).start()
  }

  private def launchGame(): Unit = {
    println("Game Started!")
    isGameInProgress = true
    SoundPlayer.stopSound("menu")
    SoundPlayer.playSound("dpoint")
    val grid: Grid = new Grid(MapReader.ReadJson(config.map.name))
    val gameWindow = Game.getWindow()

    val positions: ListBuffer[Position] = ListBuffer(
      Position(30, 30),
      Position(450, 275),
      Position(550, 600),
      Position(180, 300),
      Position(30, 650),
      Position(650, 650),
      Position(650, 30),
    )
    config.players.foreach(player => {
      val gamePlayer = new Player(player.name)
      val position = positions(Random.nextInt(positions.size))
      positions.remove(positions.indexOf(position))
      gamePlayer.tanks.addOne(new Tank(position = position, specificityConfig = player.specificity))
      grid.players.addOne(gamePlayer)
    })

    val pressedKeys = mutable.Set[Int]()
    gameWindow.setKeyManager(new KeyListener {
      override def keyTyped(e: KeyEvent): Unit = { }

      override def keyPressed(e: KeyEvent): Unit = {
        pressedKeys += e.getKeyCode
      }

      override def keyReleased(e: KeyEvent): Unit = {
        pressedKeys -= e.getKeyCode
      }
    })

    grid.drawGrid()

    //the game loop is executed in a separate thread to avoid blocking the event distribution thread (EDT), otherwise it won't work
    //this ensures that the user interface can always handle key events and remain reactive
    new Thread(() => {
      SoundPlayer.playSound("game", true)
      while (isGameInProgress) {
        grid.players.foreach { gPlayer =>
          val configForPlayer = config.players.find(_.name == gPlayer.name).get
          val upChar = configForPlayer.controls.moveUp.head.toLower
          val downChar = configForPlayer.controls.moveDown.head.toLower
          val leftChar = configForPlayer.controls.moveLeft.head.toLower
          val rightChar = configForPlayer.controls.moveRight.head.toLower
          val shootChar = configForPlayer.controls.shoot.head.toLower
          val turretLeft = configForPlayer.controls.turretLeft.head.toLower
          val turretRight = configForPlayer.controls.turretRight.head.toLower

          // converting char to KeyEvent
          val upCode = charToKeyCode(upChar)
          val downCode = charToKeyCode(downChar)
          val leftCode = charToKeyCode(leftChar)
          val rightCode = charToKeyCode(rightChar)
          val shootCode = charToKeyCode(shootChar)
          val turretLeftCode = charToKeyCode(turretLeft)
          val turretRightCode = charToKeyCode(turretRight)

          gPlayer.tanks.foreach { t =>
            var dx = 0
            var dy = 0
            if (pressedKeys.contains(upCode)) dy -= 1
            if (pressedKeys.contains(downCode)) dy += 1
            if (pressedKeys.contains(leftCode)) dx -= 1
            if (pressedKeys.contains(rightCode)) dx += 1

            val newPos = Position(t.position.x + dx * grid.cellSize, t.position.y + dy * grid.cellSize)
            if (!grid.isWallAt(newPos,50) && grid.inBounds(newPos)) {
              t.move(dx * grid.cellSize, dy * grid.cellSize)
            }

            //TODO
            // it might be cool to have slower rotations for some tanks, and it'll reflect reality better
            // if there's a VIII Maus against the AMX-30
            // var rotationFactor = 1.5 * grid.cellSize
            if (pressedKeys.contains(turretLeftCode)) t.moveTurret(true)
            if (pressedKeys.contains(turretRightCode)) t.moveTurret()
            if (pressedKeys.contains(shootCode)) t.fire()
          }
        }
        grid.update()
        Thread.sleep(20)

        //if one of the players is out of tanks (max 2 players for now)
        val looser = grid.players.filter(_.tanks.isEmpty)
        if(looser.length > 0) {
          println("THIS IS THE END")
          showEndGame(grid.players.filter(!_.tanks.isEmpty).head)
        }
      }
    }).start()
  }

  private def DisplayCurrentSound(menuWindow: FunGraphics, skipButtonX: Int, skipButtonY: Int, skipButtonWidth: Int, skipButtonHeight: Int, color: Color = Color.white): Unit = {
    val soundName = SoundPlayer.getCurrentMenuSoundName()

    val text = s"Sound: $soundName"
    val fontSize = 15
    val font = new Font("Segoe UI Emoji", Font.PLAIN, fontSize)
    menuWindow.drawString(posX = skipButtonX, posY = skipButtonY - 10, str = text, color = color, font = font)
    menuWindow.setColor(Color.GRAY)
    menuWindow.drawFillRect(skipButtonX, skipButtonY, skipButtonWidth, skipButtonHeight)
    menuWindow.drawString(posX = skipButtonX + 10, posY = skipButtonY + skipButtonHeight - 10, str = "Skip", color = Color.WHITE, font = font)
  }

  private def charToKeyCode(c: Char): Int = KeyEvent.getExtendedKeyCodeForChar(c.toInt)

  private def showOptions(): Unit = {
    val optionsWindow = Game.getWindow()
    optionsWindow.clear(new Color(140, 129, 107, 255))
    optionsWindow.mainFrame.getKeyListeners.foreach(k => optionsWindow.mainFrame.removeKeyListener(k))
  }

  private def showEndGame(winner: Player): Unit = {
    SoundPlayer.stopSound("game")
    val endWindow = Game.getWindow()
    endWindow.mainFrame.getKeyListeners.foreach(k => endWindow.mainFrame.removeKeyListener(k))
    isGameInProgress = false
    Thread.sleep(200)
    SoundPlayer.playSound("endgame")

    val darkOverlayColor = new Color(0, 0, 0, 150)
    endWindow.setColor(darkOverlayColor)
    endWindow.drawFillRect(0, 0, endWindow.width, endWindow.height)

    val font = new Font("Segoe UI Emoji", Font.PLAIN, 75)
    val message = s"${winner.name} won!"
    val metrics = endWindow.mainFrame.getFontMetrics(font)
    val textWidth = metrics.stringWidth(message)
    val textHeight = metrics.getHeight
    val textX = (endWindow.width - textWidth/2) / 2
    val textY = (endWindow.height - textHeight) / 2
    endWindow.drawString(posX = textX / 2, posY = textY, str = message, color = Color.WHITE, fontSize = 75, fontFamily = "Segoe UI Emoji")

    val instructionFont = new Font("Segoe UI Emoji", Font.PLAIN, 20)
    val instruction = "Press ESC to return to menu"
    val instrMetrics = endWindow.mainFrame.getFontMetrics(instructionFont)
    val instrWidth = instrMetrics.stringWidth(instruction)
    endWindow.drawString(posX = (endWindow.width - instrWidth) / 2, posY = textY + textHeight + 30, str = instruction, color = Color.WHITE, fontSize = 20, fontFamily = "Segoe UI Emoji")

    endWindow.mainFrame.addKeyListener(new KeyListener {
      override def keyTyped(e: KeyEvent): Unit = {}
      override def keyReleased(e: KeyEvent): Unit = {}

      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_ESCAPE) showMenu()
      }
    })
  }
  //TODO: Setup live screen capture here instead
}