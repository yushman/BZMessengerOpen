package ooo.emessi.messenger.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import ooo.emessi.messenger.utils.helpers.NotifyHelper

class NotificationService : Service() {

    private lateinit var nh: NotifyHelper

    companion object{
        private const val ACTION = "ACTION"
        private const val ACTION_OTHER = "ACTION_OTHER"
        private const val ACTION_MESSAGE = "ACTION_MESSAGE"
        private const val ACTION_ERROR = "ACTION_ERROR"
        private const val ACTION_FOREGROUND = "ACTION_FOREGROUND"
        private const val MESSAGE_FROM_JID = "MESSAGE_FROM_JID"
        private const val MESSAGE_FROM_NAME = "MESSAGE_FROM_NAME"
        private const val MESSAGE_BODY = "MESSAGE_BODY"

    }

    override fun onCreate() {
        nh = NotifyHelper(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras
        var action = ACTION_OTHER
        try {
            if (bundle != null)
            action = bundle.getString(ACTION, ACTION_OTHER)
        } catch (e: Exception){
            e.printStackTrace()
        }
        when (action){
            ACTION_MESSAGE -> actionMessage(bundle!!)
            ACTION_OTHER -> actionOther(bundle!!)
            ACTION_ERROR -> actionError(bundle!!)
            ACTION_FOREGROUND -> actionForeground(bundle!!)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun actionMessage(bundle: Bundle) {
        val from = bundle.getString(MESSAGE_FROM_JID, "")
        val name = bundle.getString(MESSAGE_FROM_NAME, "")
        val body = bundle.getString(MESSAGE_BODY, "")
        if (from.isNotEmpty() && body.isNotEmpty()){
            val notification = nh.createMessageNotification(from, name, body)
            NotificationManagerCompat.from(this)
                .notify(NotifyHelper.NOTIFICATION_ID_MESSAGE, notification)
        }
    }

    private fun actionError(bundle: Bundle) {
        val from = bundle.getString(MESSAGE_FROM_JID, "")
        val body = bundle.getString(MESSAGE_BODY, "")
        if (from.isNotEmpty() && body.isNotEmpty()) {
            val notification = nh.createErrorMessageNotification(from, body)
            NotificationManagerCompat.from(this)
                .notify(NotifyHelper.NOTIFICATION_ID_MESSAGE, notification)
        }
    }

    private fun actionForeground(bundle: Bundle) {
        val notification = nh.createForegroundNotification()
        NotificationManagerCompat.from(this)
            .notify(NotifyHelper.NOTIFICATION_ID_FOREGROUND, notification)
    }

    private fun actionOther(bundle: Bundle) {
        //TODO
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
