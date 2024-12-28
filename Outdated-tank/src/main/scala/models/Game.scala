package isc.game.outdatedtank.models

import hevs.graphics.FunGraphics
import isc.game.outdatedtank.MapReader

class Game(val config: GameConfig) {
  var isGameOver: Boolean = true
  val grid: Grid = new Grid(MapReader.ReadJson(config.map.name))
  def start(): Unit = {
    println("Game Started!")

    //TODO: Setup key listeners before while loop using config
    // config.players(X).controls.moveXX
    var tmpI = 1
    config.players.foreach(player => {
      var gamePlayer = new Player(player.name, player.color)
      gamePlayer.tanks.addOne(new Tank(position = new Position(tmpI, tmpI), color = player.color))
      grid.addPlayer(gamePlayer)
      tmpI += 5
    })

    grid.drawGrid()
    while (isGameOver) {
      grid.update()
      Thread.sleep(50)
    }
  }

  def getFG(): FunGraphics = grid.getFG()
  //TODO: Setup live screen capture here instead
}