package ooo.emessi.messenger.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ooo.emessi.messenger.managers.account.AccountManager
import ooo.emessi.messenger.service.ConnectionService
import org.koin.core.KoinComponent
import org.koin.core.get


class SystemBootStartup : BroadcastReceiver(), KoinComponent {
    private var accountManager: AccountManager = get()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val i = Intent(context, ConnectionService::class.java)
            context?.startService(i)
        }
    }
}