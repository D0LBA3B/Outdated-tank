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

  def getFG(): FunGraphics = grid.getFG()
  //TODO: Setup live screen capture here instead
}