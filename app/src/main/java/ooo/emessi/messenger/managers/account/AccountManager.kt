package ooo.emessi.messenger.managers.account

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.dto_model.account.AccountDto
import ooo.emessi.messenger.managers.AbstractManager
import ooo.emessi.messenger.settings.Settings
import ooo.emessi.messenger.utils.getHost

class AccountManager : AbstractManager(){

    var accountDto: MutableLiveData<AccountDto> = MutableLiveData()

    fun loadAccount()= CoroutineScope(Dispatchers.Main).launch {
        val acc = accountRepo.getAccount()
        placeMyJidToSettings(acc)
        accountDto.postValue(acc)
    }

    fun saveAccount(user: String, password: String) {
        val account = AccountDto(
            user,
            password,
            user.getHost())
        placeMyJidToSettings(account)
        accountRepo.saveAccount(account)
    }

    fun logOut() = CoroutineScope(Dispatchers.IO).launch{
        accountRepo.deleteAccounts()
    }

    private fun placeMyJidToSettings(myAcc: AccountDto?) {
        Settings.myAcc = myAcc
    }
}