package ooo.emessi.messenger.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

//NOT USED

class MessageCenterService : Service() {

    private val TAG = this.javaClass.simpleName

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "c")
        super.onCreate()
        
    }
}