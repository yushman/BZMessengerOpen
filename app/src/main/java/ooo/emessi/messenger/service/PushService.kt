package ooo.emessi.messenger.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ooo.emessi.messenger.utils.helpers.LogHelper
import ooo.emessi.messenger.xmpp.XMPPApi
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*


class PushService : FirebaseMessagingService(), KoinComponent {
    private val TAG = this.javaClass.simpleName
    private val xmppApi = get<XMPPApi>()

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)

        super.onNewToken(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "NewToken: " + token)
        xmppApi.registerFBToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)
        Log.d(TAG, "From: " + remoteMessage.from!!)
        Log.d(TAG, "To: " + remoteMessage.to!!)
        Log.d(TAG, "Message Id: " + remoteMessage.messageId)
        Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        LogHelper.appendLog(TAG + ":" + Date() +  "From: " + remoteMessage.from!!)
        LogHelper.appendLog(TAG + ":" + Date() +  "To: " + remoteMessage.to!!)
        LogHelper.appendLog(TAG + ":" + Date() +  "Message Id: " + remoteMessage.messageId)
        LogHelper.appendLog(TAG + ":" + Date() +  "Message Notification Body: " + remoteMessage.notification!!.body!!)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            LogHelper.appendLog(TAG + ":" + Date() +  "Message data payload: " + remoteMessage.data)
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob(remoteMessage)
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage)
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun handleNow(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
    }

    private fun scheduleJob(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)

    }
}
