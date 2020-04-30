package ooo.emessi.messenger.data.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ooo.emessi.messenger.data.database.AccountDao
import ooo.emessi.messenger.data.model.dto_model.account.AccountDto

class AccountRepo(private val dao: AccountDao) {

    fun saveAccount(accountDto: AccountDto) = CoroutineScope(Dispatchers.IO).launch {
        dao.insertBZAccount(accountDto)
    }

    suspend fun getAccount(): AccountDto? = withContext(Dispatchers.IO) {
        return@withContext dao.selectBZAccount()
    }

    fun deleteAccounts() = CoroutineScope(Dispatchers.IO).launch{
        dao.deleteBZAccounts()
    }
}