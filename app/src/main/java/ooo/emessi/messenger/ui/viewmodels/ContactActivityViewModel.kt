package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.managers.ContactsManager
import org.koin.core.KoinComponent

class ContactActivityViewModel : ViewModel(), KoinComponent {
//    val id : String = savedStateHandle["contactJid"] ?:
//    throw IllegalArgumentException("missing user id")

    private val contactsManager = ContactsManager()
    var contact: LiveData<BZContact> = contactsManager.contact

    fun loadContact(id: String){
        contactsManager.getContact(id)
    }

    fun saveContactData(jid: String, nickName: String) {
        contactsManager.addContact(jid, nickName)
    }

    fun removeContact(jid: String) {

        contactsManager.removeContact(jid)
    }
}