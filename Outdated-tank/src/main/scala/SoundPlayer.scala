package isc.game.outdatedtank

import java.io.File
import java.net.URL
import javax.sound.sampled._
import scala.collection.mutable.ListBuffer
import scala.util.Random

//interesting sites:
// https://www.sounds-resource.com/pc_computer/warthunder/
// https://sound-effects.bbcrewind.co.uk/
// https://pixabay.com/sound-effects/
object SoundPlayer {

  private var menuClips: ListBuffer[Clip] = ListBuffer.empty
  private var shootClip: Clip = null
  private var inGameClip: Clip = null
  private var dPointClip: Clip = null
  private val hitsClip: ListBuffer[Clip] = ListBuffer.empty
  private var lastHitIndex: Int = 0

  def loadSounds(): Unit = {
    // TODO MORE MENU SOUNDS WITH WAAAAAAR THUNDER CONTENT & PLAY MUSIC BTN
    menuClips.addOne(loadClip("over-there.wav"))
    menuClips.addOne(loadClip("menu-hoiiv.wav"))
    menuClips.addOne(loadClip("menu-bad.wav"))
    menuClips.addOne(loadClip("menu-wt.wav"))
    shootClip = loadClip("fire-1.wav")
    inGameClip = loadClip("game-1.wav")
    dPointClip = loadClip("attack-the-d-point-war-thunder.wav")
    hitsClip.addOne(loadClip("fr_aircraft_damaged_v1_r3_t1_mood_med.wav"))
    hitsClip.addOne(loadClip("fr_damaged_water_v1_r4_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("fr_aircraft_destroyed_v2_r4_t1_mood_med.wav"))
    hitsClip.addOne(loadClip("fr_damaged_pilot_v1_r1_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("fr_damaged_engine_v1_r3_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("fr_damaged_pilot_v1_r5_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("fr_player_killed_ally_v1_r2_t1_mood_med.wav"))
  }

  def playSound(soundId: String, loop: Boolean = false): Unit = {
    val clip = getClip(soundId)
    if (clip != null) {
      clip.setFramePosition(0)
      clip.start()

      if(loop) clip.loop(Clip.LOOP_CONTINUOUSLY)
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
      case "menu"  => getRandomMenuClip()
      case "shoot" => shootClip
      case "game" => inGameClip
      case "dpoint" => dPointClip
      case _       => null
    }
  }

  private def getRandomMenuClip(): Clip = {
    if (menuClips.nonEmpty) {
      val randomIndex = Random.nextInt(menuClips.size)
      return menuClips(randomIndex)
    }
    null
  }

  def playRandomHitSound(): Unit = {
    val alreadyPlaying = hitsClip.exists(_.isRunning)

    if (!alreadyPlaying && hitsClip.nonEmpty) {
      var newIndex = -1
      if(hitsClip.size > 1) {
        do {
          newIndex = Random.nextInt(hitsClip.size)
        } while (newIndex == lastHitIndex)
      }
      else newIndex = 0

      lastHitIndex = newIndex
      val chosenClip = hitsClip(newIndex)
      chosenClip.setFramePosition(0)
      chosenClip.start()
    }
  }

  // https://stackoverflow.com/questions/9438718/playing-wav-files-in-scala
  private def loadClip(src: String): Clip = {
    val file = getClass().getResourceAsStream(s"/sfx/${src}")
    val audioIn = AudioSystem.getAudioInputStream(file)
    val clip = AudioSystem.getClip
    clip.open(audioIn)
    clip
  }
}
