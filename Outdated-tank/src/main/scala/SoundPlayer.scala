package isc.game.outdatedtank

import java.net.URL
import javax.sound.sampled._
import scala.collection.mutable.ListBuffer
import scala.util.Random

//interesting sites:
// https://www.sounds-resource.com/pc_computer/warthunder/
// https://sound-effects.bbcrewind.co.uk/
// https://pixabay.com/sound-effects/
object SoundPlayer {

  private var menuClip: Clip = null
  private var shootClip: Clip = null
  private var inGameClip: Clip = null
  private var dPointClip: Clip = null
  private val hitsClip: ListBuffer[Clip] = ListBuffer.empty
  private var hitIndex: Int = 0

  def loadSounds(): Unit = {
    menuClip = loadClip("https://local-host.dev/sfx/over-there.wav")
    shootClip = loadClip("https://local-host.dev/sfx/fire-1.wav")
    inGameClip = loadClip("https://local-host.dev/sfx/game-1.wav")
    dPointClip = loadClip("https://local-host.dev/sfx/attack-the-d-point-war-thunder.wav")
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_aircraft_damaged_v1_r3_t1_mood_med.wav"))
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_damaged_water_v1_r4_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_aircraft_destroyed_v2_r4_t1_mood_med.wav"))
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_damaged_pilot_v1_r1_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_damaged_engine_v1_r3_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_damaged_pilot_v1_r5_t1_mood_high.wav"))
    hitsClip.addOne(loadClip("https://local-host.dev/sfx/fr_player_killed_ally_v1_r2_t1_mood_med.wav"))
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
      case "dpoint" => dPointClip
      case _       => null
    }
  }

  def playRandomHitSound(): Unit = {
    val alreadyPlaying = hitsClip.exists(_.isRunning)

    if (!alreadyPlaying && hitsClip.nonEmpty) {
    if(hitIndex >= hitsClip.size) hitIndex = 0

      val chosenClip = hitsClip(hitIndex)
      chosenClip.setFramePosition(0)
      chosenClip.start()
      hitIndex += 1
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
  // TODO set damage levels on a tank and play increasingly alarming sounds
}
