package ooo.emessi.messenger.data.database

import androidx.room.*
import ooo.emessi.messenger.data.model.dto_model.account.AccountDto

@Dao
interface AccountDao{

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertBZAccount(accountDto: AccountDto)

    @Update
    fun updateBZAccount(accountDto: AccountDto)

    @Delete
    fun deleteBZAccount(accountDto: AccountDto)

    @Query("Delete from bz_account")
    fun deleteBZAccounts()

    @Query("Select * from bz_account limit 1")
    fun selectBZAccount(): AccountDto?
}