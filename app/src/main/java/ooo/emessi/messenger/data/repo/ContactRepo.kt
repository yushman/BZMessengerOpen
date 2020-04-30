package ooo.emessi.messenger.data.repo

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.database.ContactDao
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto

class ContactRepo(private val dao: ContactDao) {

    fun saveContact(contactDto: ContactDto) = CoroutineScope(Dispatchers.IO).launch {
        dao.insertContact(contactDto)
    }

    fun saveContacts(contactDtos: List<ContactDto>) = CoroutineScope(Dispatchers.IO).launch {
        dao.insertContacts(contactDtos)
    }

    fun deleteContact(contactDto: ContactDto) = CoroutineScope(Dispatchers.IO).launch {
        dao.deleteContact(contactDto)
    }

    fun deleteContactById(jid: String)= CoroutineScope(Dispatchers.IO).launch{
        dao.deleteContactByJid(jid)
    }

    fun loadContacts(): LiveData<List<ContactDto>> {
        return dao.selectAllContacts()
    }

    fun getContactById(jid: String): ContactDto? {
        return dao.selectContactById(jid)
    }

    fun getContacts(): List<ContactDto> = dao.selectAllContactsSync()

    fun prepopulateDb() = CoroutineScope(Dispatchers.IO).launch{
        val list = mutableListOf<ContactDto>()
        list.add(ContactDto("i.yushenkov@mossales.ru", "ivan", null))
        list.add(ContactDto("fff@mossales.ru", "fff", null))
        list.add(ContactDto("ddd@mossales.ru", "ddd", null))
        list.add(ContactDto("sss@mossales.ru", "sss", null))
        list.add(ContactDto("test11@conference.mossales.ru", "aaa", null))
        list.forEach { dao.insertContact(it) }
    }

    fun updateContact(contactDto: ContactDto) {
        dao.update(contactDto)
    }

}