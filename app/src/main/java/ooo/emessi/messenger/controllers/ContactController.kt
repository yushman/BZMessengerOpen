package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.repo.ContactRepo
import org.koin.core.KoinComponent
import org.koin.core.get

class ContactController: KoinComponent {

    private val contactRepo = get<ContactRepo>()
    val contactDto: MutableLiveData<ContactDto> = MutableLiveData()

    fun loadContact(jid: String){
        contactDto.postValue(contactRepo.getContactById(jid))

    }

    fun saveContactData(jid: String, nickName: String) {
        val contact = contactDto.value
        if (contact != null) {
            if (contact.contactJid == jid) updateContactInfo(contact, nickName)
            else replaceContact(contact, jid, nickName)
        } else {
            addNewContact(jid, nickName)
        }
    }

    fun removeContact(jid: String) {
        contactRepo.deleteContactById(jid)
    }

    private fun addNewContact(jid: String, nickName: String) {
        contactRepo.saveContact(ContactDto(jid, nickName))
    }

    private fun updateContactInfo(
        contact: ContactDto,
        nickName: String
    ) {
        contactRepo.updateContact(contact.copy(name = nickName))
    }

    private fun replaceContact(
        contact: ContactDto,
        jid: String,
        nickName: String
    ){
        contactRepo.deleteContact(contact)
        contactRepo.saveContact(ContactDto(jid, nickName))
    }
}