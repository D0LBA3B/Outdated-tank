package isc.game.outdatedtank

import java.net.URL
import javax.sound.sampled._

object SoundPlayer {

  private var menuClip: Clip = null
  private var shootClip: Clip = null
  private var inGameClip: Clip = null

  def loadSounds(): Unit = {
    menuClip = loadClip("https://local-host.dev/sfx/over-there.wav")
    shootClip = loadClip("https://local-host.dev/sfx/fire-1.wav")
    inGameClip = loadClip("https://local-host.dev/sfx/game-1.wav")
  }

  def playSound(soundId: String): Unit = {
    val clip = getClip(soundId)
    if (clip != null) {
      clip.setFramePosition(0)
      clip.start()
    }
  }

  def stopSound(soundId: String): Unit = {
    val clip = getClip(soundId)
    if (clip != null && clip.isRunning) {
      clip.stop()
      clip.setFramePosition(0)
    }
  }

  private def getClip(soundId: String): Clip = {
    soundId match {
      case "menu"  => menuClip
      case "shoot" => shootClip
      case "game" => inGameClip
      case _       => null
    }
  }

  // https://stackoverflow.com/questions/9438718/playing-wav-files-in-scala
  private def loadClip(urlString: String): Clip = {
    val url = new URL(urlString)
    val audioIn = AudioSystem.getAudioInputStream(url)
    val clip = AudioSystem.getClip
    clip.open(audioIn)
    clip
  }
}
