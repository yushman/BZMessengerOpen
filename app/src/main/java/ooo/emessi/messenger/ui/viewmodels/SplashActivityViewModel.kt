package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount
import ooo.emessi.messenger.managers.AccountManager
import org.koin.core.KoinComponent
import org.koin.core.get

class SplashActivityViewModel : ViewModel(), KoinComponent{
    private val accountManager: AccountManager = get()

    val account: LiveData<BZAccount> = accountManager.account

    fun loadAccount() {
        accountManager.loadAccount()
    }


}