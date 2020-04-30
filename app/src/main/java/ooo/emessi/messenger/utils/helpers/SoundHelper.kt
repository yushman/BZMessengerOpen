package ooo.emessi.messenger.utils.helpers

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import ooo.emessi.messenger.R
import ooo.emessi.messenger.constants.Constants

object SoundHelper {
    fun playSendSound(context: Context){
        val mp = MediaPlayer.create(context, R.raw.message_send_alert)
        mp.start()
    }

    fun vibrate(context: Context) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(
                VibrationEffect.createOneShot(
                    Constants.VIBRATE_LENGTH,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            //deprecated in API 26
            v.vibrate(Constants.VIBRATE_LENGTH)
        }
    }
}