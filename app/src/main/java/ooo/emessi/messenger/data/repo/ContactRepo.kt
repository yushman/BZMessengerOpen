package ooo.emessi.messenger.data.repo

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.database.BZDatabase
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact

class ContactRepo (context: Context){

    private val bzDatabase = BZDatabase.getInstance(context)
    private val dao = bzDatabase.contactDao()

//    init {
//        prepopulateDb()
//    }

    fun saveContact(contact: BZContact) = CoroutineScope(Dispatchers.IO).launch{
        dao.insertContact(contact)
    }

    fun saveContacts(contacts: List<BZContact>) = CoroutineScope(Dispatchers.IO).launch{
        dao.insertContacts(contacts)
    }

    fun deleteContact(contact: BZContact) = CoroutineScope(Dispatchers.IO).launch{
        dao.deleteContact(contact)
    }

    fun deleteContactById(jid: String)= CoroutineScope(Dispatchers.IO).launch{
        dao.deleteContactByJid(jid)
    }

    fun loadContacts(): LiveData<List<BZContact>>{
        return dao.selectAllContacts()
    }

    fun getContacts(): List<BZContact> {
            return dao.selectAllContactsSync()
    }



    fun getContactById(jid: String): BZContact?{
        return dao.selectContactById(jid)
    }

    fun prepopulateDb() = CoroutineScope(Dispatchers.IO).launch{
        val list = mutableListOf<BZContact>()
        list.add(BZContact("i.yushenkov@mossales.ru", "ivan", null))
        list.add(BZContact("fff@mossales.ru", "fff", null))
        list.add(BZContact("ddd@mossales.ru", "ddd", null))
        list.add(BZContact("sss@mossales.ru", "sss", null))
        list.add(BZContact("test11@conference.mossales.ru", "aaa", null))
        list.forEach { dao.insertContact(it) }
    }

}