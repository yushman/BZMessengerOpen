package ooo.emessi.messenger.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto

@Dao
interface ContactDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contactDto: ContactDto)

    @Delete
    fun deleteContact(contactDto: ContactDto)

    @Query("Delete from contacts where contactJid like :contactJid")
    fun deleteContactByJid(contactJid: String)

    @Query("Select * from contacts")
    fun selectAllContacts(): LiveData<List<ContactDto>>

    @Query("Select * from contacts")
    fun selectAllContactsSync(): List<ContactDto>

    @Query("Select * from contacts where contactJid like :contactJid")
    fun selectContactById(contactJid: String): ContactDto?

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertContacts(contactDtos: List<ContactDto>)

    @Update
    fun update(contactDto: ContactDto)
}
