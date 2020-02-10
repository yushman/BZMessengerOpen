package ooo.emessi.messenger.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ooo.emessi.messenger.service.BZChatService
import ooo.emessi.messenger.service.ConnectionService.Companion.AUTH_DONE
import ooo.emessi.messenger.service.ConnectionService.Companion.AUTH_FAILED
import ooo.emessi.messenger.service.ConnectionService.Companion.EMPTY_ACCOUNT
import ooo.emessi.messenger.service.ConnectionService.Companion.RESULT
import ooo.emessi.messenger.ui.activities.LoginActivity
import ooo.emessi.messenger.ui.activities.MainActivity

class ConnectionReceiver : BroadcastReceiver() {
    companion object{
        const val REASON = "REASON"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val result = intent.extras!!.getString(RESULT)
        when(result){
            AUTH_DONE -> startMainActivity(context)
            AUTH_FAILED -> starLoginActivity(context, AUTH_FAILED)
            EMPTY_ACCOUNT -> starLoginActivity(context, EMPTY_ACCOUNT)
        }
    }

    private fun starLoginActivity(context: Context, reason: String) {
        val i = Intent(context, LoginActivity::class.java)
        i.putExtra(REASON, reason)
        context.startActivity(i)
    }

    private fun startMainActivity(context: Context) {
        context.startService(Intent(context, BZChatService::class.java))
        context.startActivity(Intent(context, MainActivity::class.java))
    }
}
