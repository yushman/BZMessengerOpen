package ooo.emessi.messenger.data.database

import androidx.room.*
import ooo.emessi.messenger.data.model.bz_model.account.BZAccount

@Dao
interface AccountDao{

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertBZAccount(bzAccount: BZAccount)

    @Update
    fun updateBZAccount(bzAccount: BZAccount)

    @Delete
    fun deleteBZAccount(bzAccount: BZAccount)

    @Query("Select * from bz_account limit 1")
    fun selectBZAccount(): BZAccount?
}