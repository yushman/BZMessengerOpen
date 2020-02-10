package ooo.emessi.messenger.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log



class NetworkStateReceiver : BroadcastReceiver() {

    private val TAG = this.javaClass.simpleName

    private val ACTION_START = 1
    private val ACTION_STOP = 2
    private val ACTION_TEST = 3

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        var serviceAction = 0

        val cm = context!!
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // background data setting has changed
        if (ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED == action) {

            // if background data gets deactivated, just stop the service now
            serviceAction = if (!cm.backgroundDataSetting) {
                Log.w(TAG, "background data disabled!")
                ACTION_STOP
            } else {
                Log.w(TAG, "background data enabled!")
                // start message center
                ACTION_START
                // notify ping manager that connection type has changed
                // AndroidAdaptiveServerPingManager.onConnected()
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION == action) {
            // TODO handle FAILOVER_CONNECTION

            val info = cm.activeNetworkInfo
            if (info == null) {
                Log.d(TAG, "no network available!")
                serviceAction = ACTION_STOP
            } else if (info.isConnected) {
                Log.d(TAG, "connected to network!")

//                if (info.type == ConnectivityManager.TYPE_MOBILE && !shouldReconnect(context)) {
//                    Log.w(TAG, "throttling on mobile network")
//                    return
//                }

                // test connection or reconnect
                serviceAction = ACTION_TEST
                // notify ping manager that connection type has changed
                //AndroidAdaptiveServerPingManager.onConnected()
            }
        }// connectivity status has changed

//        when (serviceAction) {
//            ACTION_START ->
//                // start message center
//                MessageCenterService.start(context)
//            ACTION_STOP ->
//                // stop message center
//                MessageCenterService.stop(context)
//            ACTION_TEST ->
//                // connection test
//                MessageCenterService.test(context, true)
//        }
    }

//    private fun shouldReconnect(context: Context): Boolean {
//        // check if some activity is holding to the message center
//        // or there is a pending push notification
//        if (Kontalk.get().hasReference() || Preferences.getLastPushNotification() > 0)
//            return true
//
//        val lastConnect = Preferences.getLastConnection()
//
//        // no last connection registered
//        if (lastConnect < 0)
//            return true
//
//        val now = System.currentTimeMillis()
//        val diff = Preferences.getWakeupTimeMillis(
//            context,
//            MessageCenterService.MIN_WAKEUP_TIME
//        )
//
//        return now - lastConnect >= diff
//    }
}