package isc.game.outdatedtank.models

import com.typesafe.config._

import java.awt.Color
import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._

case class Resolution(cellSize: Int)

case class Battlefield(name: String, terrainColor: String)

case class ControlsConfig(moveUp: String,
                           moveDown: String,
                           moveLeft: String,
                           moveRight: String,
                           shoot: String,
                           turretLeft: String,
                           turretRight: String)

case class SpecificityConfig(name: String, fireCooldown: Long, ammoDamage: Int, ammoBounceLeft: Int, health: Int)

case class PlayerConfig(name: String, controls: ControlsConfig, specificity: SpecificityConfig)

case class GameConfig(resolution: Resolution, map: Battlefield, players: Seq[PlayerConfig])

// https://medium.com/@ramkarnani24/reading-configurations-in-scala-f987f839f54d
object GameConfig {
  private lazy val instance: GameConfig = load()

  private def load(): GameConfig = {
    val config = ConfigFactory.load()

    val resolution = Resolution(
      cellSize = config.getInt("isc.game.outdatedtank.game.window.cellSize")
    )

    val battlefield = Battlefield(
      name = mapNameFinder(config.getString("isc.game.outdatedtank.game.map.name")),
      terrainColor = config.getString("isc.game.outdatedtank.game.map.terrainColor"),
    )

    val specificitiesConfig = config.getConfigList("isc.game.outdatedtank.game.specificities").asScala
    val specificities = specificitiesConfig.map { specificity =>
      SpecificityConfig(
        name = specificity.getString("name"),
        fireCooldown = specificity.getLong("fireCooldown"),
        ammoDamage = specificity.getInt("ammoDamage"),
        ammoBounceLeft = specificity.getInt("ammoBounceLeft"),
        health = specificity.getInt("health"))
    }

    val playersConfig = config.getConfigList("isc.game.outdatedtank.game.players").asScala
    val players = playersConfig.map { player =>
      PlayerConfig(
        name = player.getString("name"),
        controls = ControlsConfig(
          moveUp = player.getString("controls.moveUp"),
          moveDown = player.getString("controls.moveDown"),
          moveLeft = player.getString("controls.moveLeft"),
          moveRight = player.getString("controls.moveRight"),
          shoot = player.getString("controls.shoot"),
          turretLeft = player.getString("controls.turretLeft"),
          turretRight = player.getString("controls.turretRight")
        ),
        specificity = specificities(specificities.indexWhere(c => c.name == player.getString("specificity")))
      )
    }.toSeq

    GameConfig(resolution = resolution, map = battlefield, players = players)
  }

  def get: GameConfig = instance

  private def AWTcolorconverter(input: String): Color = {
    try {
      // HEXA
      if (input.startsWith("#") || input.matches("^[0-9a-fA-F]{6}$")) {
        return Color.decode(input.replace("#", ""))
      }

      // RGB
      if (input.toLowerCase.startsWith("rgb")) {
        val rgbPattern = """rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)""".r
        input.toLowerCase match {
          case rgbPattern(r, g, b) =>
            return new Color(r.toInt, g.toInt, b.toInt)
          case _ => throw new IllegalArgumentException("Invalid RGB format")
        }
      }

      // TEXT
      input.toUpperCase match {
        case "RED"    => Color.RED
        case "GREEN"  => Color.GREEN
        case "BLUE"   => Color.BLUE
        case "BLACK"  => Color.BLACK
        case "WHITE"  => Color.WHITE
        case "YELLOW" => Color.YELLOW
        case "GRAY"   => Color.GRAY
        case "ORANGE" => Color.ORANGE
        case "PINK"   => Color.PINK
        case "CYAN"   => Color.CYAN
        case "MAGENTA"=> Color.MAGENTA
        case _        => throw new IllegalArgumentException("Unknown color name")
      }
    } catch {
      case e: Exception =>
        println(s"Error parsing color input: $input. Using default color RED.")
        Color.RED
    }
  }

  private def mapNameFinder(input: String): String = {
    val mapFileName = s"$input.json"
    val mapPath = Paths.get(new URL(s"https://raw.githubusercontent.com/D0LBA3B/Outdated-tank/develop/Outdated-tank/src/main/resources/Maps/map1.json"), mapFileName)

    if (Files.exists(mapPath)) mapFileName
    else "map1.json"
  }
}