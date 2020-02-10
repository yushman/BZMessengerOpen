package ooo.emessi.messenger.utils.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ooo.emessi.messenger.R
import ooo.emessi.messenger.ui.activities.MainActivity
import ooo.emessi.messenger.ui.activities.MucLightChatActivity
import ooo.emessi.messenger.ui.activities.NewMainActivity
import ooo.emessi.messenger.ui.activities.SingleChatActivity
import ooo.emessi.messenger.utils.isMultiChat


class NotifyHelper (val context: Context) {

    init {
        createChannels(context)
    }



    private fun createChannels(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val resources = context.resources
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            createMessageChannel(notificationManager)
//            createDownloadChannel(resources, notificationManager)
//            createUploadChannel(resources, notificationManager)
            createForegroundChannel(notificationManager)
            createOtherChannel(notificationManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOtherChannel(
        notificationManager: NotificationManager
    ) {
        val name = "Other"
        val description = "Other notifications"
        val channel = NotificationChannel(
            CHANNEL_OTHER, name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = description
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createMessageChannel(
        notificationManager: NotificationManager
    ) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val name = "Messages"
        val description = "Incoming messages"
        val channel = NotificationChannel(
            CHANNEL_INCOMING_MESSAGE, name,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = description
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.setShowBadge(true)
        channel.setSound(alarmSound, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createForegroundChannel(
        notificationManager: NotificationManager
    ) {
        val name = "Foreground"
        val description = "Required to keep BZMessenger service alive"
        val channel = NotificationChannel(
            CHANNEL_FOREGROUND, name,
            NotificationManager.IMPORTANCE_MIN
        )
        channel.description = description
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.setShowBadge(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun supportsBigNotifications(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
    }

    fun supportsDirectReply(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    fun createMessageNotification(from: String, name: String, body: String):Notification {
        //TODO route to chat activity by from

        val ri = Intent(this.context, NewMainActivity::class.java)
        ri.putExtra("JID", from)
        val pi = PendingIntent.getActivity(context, 0, ri, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return NotificationCompat.Builder(context,
            CHANNEL_INCOMING_MESSAGE
        )
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(name)
            .setContentText(body)
            .setStyle(NotificationCompat.MessagingStyle("Me")
                .addMessage(body, System.currentTimeMillis(), name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setColorized(true)
            .setGroup("MESSAGEGROUP")
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
    }

    fun createForegroundNotification(): Notification{
        val ri = Intent(this.context, NewMainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, ri, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(context,
            CHANNEL_FOREGROUND
        )
            .setSmallIcon(R.drawable.flash)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setTicker("BZ Messenger is running in the background")
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("Tap to open")
            .setContentIntent(pi)
            .build()
    }

    fun createErrorMessageNotification(from: String, body: String): Notification {
        val ri = Intent(this.context, NewMainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, ri, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return NotificationCompat.Builder(
            context,
            CHANNEL_OTHER
        )
            .setSmallIcon(R.drawable.ic_error_black_24dp)
            .setContentTitle("Error from $from")
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setColorized(true)
            .build()
    }

    companion object{

        const val NOTIFICATION_ID_FOREGROUND      = 110
        const val NOTIFICATION_ID_MESSAGE = 111
        const val NOTIFICATION_ID_OTHER = 112

        const val CHANNEL_INCOMING_MESSAGE = "incoming_message"
        const val CHANNEL_FOREGROUND = "message_foreground"
        const val CHANNEL_OTHER = "other"
    }

}