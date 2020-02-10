package ooo.emessi.messenger.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact

@Dao
interface ContactDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: BZContact)

    @Delete
    fun deleteContact(contact: BZContact)

    @Query("Delete from contacts where contactJid like :contactJid")
    fun deleteContactByJid(contactJid: String)

    @Query("Select * from contacts")
    fun selectAllContacts(): LiveData<List<BZContact>>

    @Query("Select * from contacts")
    fun selectAllContactsSync(): List<BZContact>

    @Query("Select * from contacts where contactJid like :contactJid")
    fun selectContactById(contactJid: String): BZContact?

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(contacts: List<BZContact>)
}
