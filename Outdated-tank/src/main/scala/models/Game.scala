package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import isc.game.outdatedtank.MapReader
import java.awt.Dimension

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
    } else if (width > 0 && height > 0 && (gameWindow.mainFrame.getWidth != width || gameWindow.mainFrame.getHeight != height)) {
      // window content resizing not working? TODO
      //gameWindow.mainFrame.setSize(width, height)
      //val contentPane = gameWindow.mainFrame.getContentPane
      //contentPane.setSize(width, height)
      //contentPane.repaint()
      //gameWindow.clear()
      gameWindow.mainFrame.dispose()
      gameWindow = new FunGraphics(width, height, title)
    }
    gameWindow
  }
}

class Game private(val config: GameConfig) {
  var isGameOver: Boolean = true

  def start(): Unit = {
    //showMenu()
    launchGame()
  }

  private def showMenu(): Unit = {
      val menuWidth = 300
      val menuHeight = 200
      val buttonX = 100
      val buttonY = 100
      val buttonWidth = 100
      val buttonHeight = 50

      val menuWindow = Game.getWindow(menuWidth, menuHeight)
      menuWindow.clear()

      menuWindow.setColor(java.awt.Color.GREEN)
      menuWindow.drawFillRect(buttonX, buttonY, buttonWidth, buttonHeight)

      menuWindow.setColor(java.awt.Color.WHITE)
      menuWindow.drawString(buttonX + 20, buttonY + 30, "FIGHT !")

      menuWindow.mainFrame.addMouseListener(new java.awt.event.MouseAdapter {
        override def mouseClicked(e: java.awt.event.MouseEvent): Unit = {
          val mouseX = e.getX
          val mouseY = e.getY

          if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
              mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
            println("LAUNCH THE GAME...")
            launchGame()
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