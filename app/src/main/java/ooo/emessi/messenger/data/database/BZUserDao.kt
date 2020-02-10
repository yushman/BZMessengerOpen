package ooo.emessi.messenger.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ooo.emessi.messenger.data.model.bz_model.user.BZUser

@Dao
interface BZUserDao {

    @Insert
    fun insertBZUser(bzUser: BZUser)

    @Delete
    fun deleteBZUser(bzUser: BZUser)

    @Query("Select * from bz_users")
    fun selectAllBZUsers(): List<BZUser>

    @Query("Select * from bz_users where userJid like :jid")
    fun selectBZUserById(jid: String): BZUser
}