package ooo.emessi.messenger.data.repo

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ooo.emessi.messenger.data.database.BZDatabase
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount

class AccountRepo (context: Context) {
    private val dataBase = BZDatabase.getInstance(context)
    private val dao = dataBase.accountDao()

    fun saveAccount(bzAccount: BZAccount) = CoroutineScope(Dispatchers.IO).launch {
        dao.insertBZAccount(bzAccount)
    }

    suspend fun getAccount(): BZAccount? = withContext(Dispatchers.IO){
        return@withContext dao.selectBZAccount()
    }

    fun deleteAccount() = CoroutineScope(Dispatchers.IO).launch{
        val acc = getAccount()
        dao.deleteBZAccount(acc!!)
    }
}