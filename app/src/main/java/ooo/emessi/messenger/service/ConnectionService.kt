package ooo.emessi.messenger.service

import android.content.Intent
import android.os.Build
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount
import ooo.emessi.messenger.managers.AccountManager
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.koin.core.KoinComponent
import org.koin.core.get

//NOT USED

class ConnectionService : LifecycleService(), KoinComponent {

    companion object{
        private const val STANDART_CONNECTION_DELAY = 1000L
        const val ACTION_CONNECTION = "ru.mossales.BZMessenger.action.CONNECTION"
        const val RESULT = "connection.RESULT"
        const val AUTH_DONE = "connection.AUTH_DONE"
        const val AUTH_FAILED = "connection.AUTH_FAILED"
        const val EMPTY_ACCOUNT = "connection.EMPTY_ACCOUNT"
    }

    private val accountManager: AccountManager = get()

    override fun onCreate() {
        accountManager.loadAccount()
        accountManager.account.observe(this, Observer { tryConnect(it) })
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    private fun tryConnect(it: BZAccount?) = CoroutineScope(Dispatchers.Default).launch {
        val i = Intent(ACTION_CONNECTION)
        withContext(Dispatchers.IO){
            if (it != null){
                val connection = XMPPConnectionApi.setupConnection(it.host, it.userJid, it.password)
                connection.connect()
                delay(STANDART_CONNECTION_DELAY)
                connection.login()
                delay(STANDART_CONNECTION_DELAY)

                if (connection.isAuthenticated) {
                    i.putExtra(RESULT, AUTH_DONE)
                } else {
                    i.putExtra(RESULT, AUTH_FAILED)

                }
            } else {
                i.putExtra(RESULT, EMPTY_ACCOUNT)

            }
        }
        sendBroadcast(i)
        val si = Intent(this@ConnectionService, BZChatService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(si)
        else startService(si)

    stopSelf()
    }
}
