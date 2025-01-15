package isc.game.outdatedtank

import javax.sound.sampled._
import scala.collection.mutable.ListBuffer
import scala.util.Random

case class AudioClip(clip: Clip, name: String, framePosition: Int = 0)

//interesting sites:
// https://www.sounds-resource.com/pc_computer/warthunder/
// https://sound-effects.bbcrewind.co.uk/
// https://pixabay.com/sound-effects/
object SoundPlayer {

  private val menuClips: ListBuffer[AudioClip] = ListBuffer.empty
  private var shootClip: AudioClip = null
  private var inGameClip: AudioClip = null
  private var dPointClip: AudioClip = null
  private var endGameClip: AudioClip = null
  private val hitsClip: ListBuffer[AudioClip] = ListBuffer.empty
  private var lastHitIndex: Int = 0
  private var lastMenuIndex: Int = 0
  private var isLoadingFailed: Boolean = false

  def loadSounds(): Unit = {
    menuClips.addOne(loadClip("over-there.wav", "Over There"))
    menuClips.addOne(loadClip("menu-fr-1.wav", "Vous n'aurez pas l'Alsace et la Lorraine"))
    menuClips.addOne(loadClip("menu-hoiiv.wav", "HOI IV"))
    menuClips.addOne(loadClip("menu-wt.wav", "War Thunder"))
    menuClips.addOne(loadClip("menu-hoiiv-2.wav", "HOI IV 2"))
    menuClips.addOne(loadClip("menu-sw-1.wav", "Rufst du, mein Vaterland"))
    menuClips.addOne(loadClip("varsovienne-warszawianka.wav", "warszawianka"))
    shootClip = loadClip("fire-1.wav", "Hit")
    inGameClip = loadClip("game-1.wav", "Game")
    dPointClip = loadClip("attack-the-d-point-war-thunder.wav", "D-Point")
    endGameClip = loadClip("fr_bomb_success_v3_r5_t1_mood_high.wav", "B1")
    hitsClip.addOne(loadClip("fr_aircraft_damaged_v1_r3_t1_mood_med.wav", "B1"))
    hitsClip.addOne(loadClip("fr_damaged_water_v1_r4_t1_mood_high.wav", "B2"))
    hitsClip.addOne(loadClip("fr_aircraft_destroyed_v2_r4_t1_mood_med.wav", "B3"))
    hitsClip.addOne(loadClip("fr_damaged_pilot_v1_r1_t1_mood_high.wav", "B4"))
    hitsClip.addOne(loadClip("fr_damaged_engine_v1_r3_t1_mood_high.wav", "B5"))
    hitsClip.addOne(loadClip("fr_damaged_pilot_v1_r5_t1_mood_high.wav", "B6"))
    hitsClip.addOne(loadClip("fr_player_killed_ally_v1_r2_t1_mood_med.wav", "B7"))
  }

  def playSound(soundId: String, loop: Boolean = false): Unit = {
    if(isLoadingFailed) return

    val clip = getClip(soundId)
    if (clip != null) {
      clip.clip.setFramePosition(clip.framePosition)
      clip.clip.start()

      if(loop) clip.clip.loop(Clip.LOOP_CONTINUOUSLY)
    }
  }

  def stopSound(soundId: String): Unit = {
    if(isLoadingFailed) return

    val clip = getClip(soundId).clip
    if (clip != null && clip.isRunning) {
      clip.stop()
      clip.setFramePosition(0)
    }
  }

  private def getClip(soundId: String): AudioClip = {
    soundId match {
      case "menu" => menuClips.find(_.clip.isRunning).getOrElse(getRandomMenuClip())
      case "shoot" => shootClip
      case "game" => inGameClip
      case "dpoint" => dPointClip
      case "endgame" => endGameClip
      case _ => null
    }
  }

  private def getRandomMenuClip(lastIndex: Int = -1): AudioClip = {
    if (menuClips.nonEmpty) {
      if(lastIndex != -1) {
        var index = 0
        do {
          index = Random.nextInt(menuClips.size)
        } while (index == lastIndex)
        return menuClips(index)
      }
      else {
        val randomIndex = Random.nextInt(menuClips.size)
        return menuClips(randomIndex)
      }
    }
    null
  }

  def skipMenuClip(): Unit = {
    if(isLoadingFailed) return

    val currentClip = menuClips.find(_.clip.isRunning).getOrElse(menuClips(0))
    if(currentClip != null) {
      currentClip.clip.stop()
      val nextClip = getRandomMenuClip(menuClips.indexOf(currentClip))
      nextClip.clip.setFramePosition(0)
      nextClip.clip.start()
      nextClip.clip.loop(Clip.LOOP_CONTINUOUSLY)
    }
  }

  def getCurrentMenuSoundName: String = {
    if(isLoadingFailed) return "No sound"

    //how to access the filename directly in clip?????
    menuClips.find(_.clip.isRunning).map(_.name).getOrElse("No sound played")
  }

  def playRandomHitSound(): Unit = {
    val alreadyPlaying = hitsClip.map(_.clip).exists(_.isRunning)

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
      chosenClip.clip.setFramePosition(chosenClip.framePosition)
      chosenClip.clip.start()
    }
  }

  // https://stackoverflow.com/questions/9438718/playing-wav-files-in-scala
  private def loadClip(src: String, name: String): AudioClip = {
    try {
      val file = getClass.getResourceAsStream(s"/sfx/${src}")
      val audioIn = AudioSystem.getAudioInputStream(file)
      val clip = AudioSystem.getClip
      clip.open(audioIn)

      val framePosition = if (src == "fire-1.wav") 40000 else 0
      isLoadingFailed = false
      return AudioClip(clip, name, framePosition)
    } catch {
      case e: Exception =>
        println(s"Failed to load audio clip using default system clip: ${e.getMessage}")
    }

    //if we can't open the clip with the basic audio system, we'll try to open it with one of the others on the system
    val mixers = AudioSystem.getMixerInfo
    for (mixerInfo <- mixers) {
      try {
        println(s"Trying to load audio with mixer: ${mixerInfo.getName}")
        val file = getClass.getResourceAsStream(s"/sfx/${src}")
        val audioIn = AudioSystem.getAudioInputStream(file)
        val mixer = AudioSystem.getMixer(mixerInfo)
        val clip = mixer.getLine(new DataLine.Info(classOf[Clip], audioIn.getFormat)).asInstanceOf[Clip]
        clip.open(audioIn)

        val framePosition = if (src == "fire-1.wav") 40000 else 0
        isLoadingFailed = false
        return AudioClip(clip, src, framePosition)
      } catch {
        case e: Exception =>
          println(s"Failed with mixer: ${mixerInfo.getName}, error: ${e.getMessage}")
      }
    }
    isLoadingFailed = true
    AudioClip(clip = null, name = "", framePosition = 0)
  }
}
