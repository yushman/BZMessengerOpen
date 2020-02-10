package ooo.emessi.messenger.managers

import android.accounts.Account
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount
import ooo.emessi.messenger.data.repo.AccountRepo
import ooo.emessi.messenger.settings.Settings
import ooo.emessi.messenger.utils.getHost
import org.koin.core.KoinComponent
import org.koin.core.get

class AccountManager : KoinComponent{

    private val accountRepo: AccountRepo = get()

    var account: MutableLiveData<BZAccount> = MutableLiveData()

    fun loadAccount()= CoroutineScope(Dispatchers.Main).launch {
        val acc = accountRepo.getAccount()
        placeMyJidToSettings(acc)
        account.postValue(acc)
    }

    fun saveAccount(user: String, password: String) {
        val account = BZAccount(
            user,
            password,
            user.getHost())
        placeMyJidToSettings(account)
        accountRepo.saveAccount(account)
    }

    fun logOut() = CoroutineScope(Dispatchers.IO).launch{
        //accountRepo.deleteAcc()
    }

    private fun placeMyJidToSettings(myAcc: BZAccount?){
        Settings.myAcc = myAcc
    }
}