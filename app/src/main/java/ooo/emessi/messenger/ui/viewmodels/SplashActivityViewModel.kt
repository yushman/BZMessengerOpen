package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.dto_model.account.AccountDto
import ooo.emessi.messenger.managers.account.AccountManager
import org.koin.core.KoinComponent
import org.koin.core.get

class SplashActivityViewModel : ViewModel(), KoinComponent{
    private val accountManager: AccountManager = get()

    val accountDto: LiveData<AccountDto> = accountManager.accountDto

    fun loadAccount() {
        accountManager.loadAccount()
    }


}