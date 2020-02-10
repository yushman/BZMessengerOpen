package ooo.emessi.messenger.utils.helpers

import android.content.Context
import android.media.MediaPlayer
import ooo.emessi.messenger.R

object SoundHelper {
    fun playSendSound(context: Context){
        val mp = MediaPlayer.create(context, R.raw.message_send_alert)
        mp.start()
    }
}