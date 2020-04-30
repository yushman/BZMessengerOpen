package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.controllers.ContactController
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import org.koin.core.KoinComponent

class ContactActivityViewModel : ViewModel(), KoinComponent {
//    val id : String = savedStateHandle["contactJid"] ?:
//    throw IllegalArgumentException("missing user id")

    private val controller = ContactController()
    val contactDto: LiveData<ContactDto> = controller.contactDto

    fun loadContact(id: String){
        controller.loadContact(id)
    }

    fun saveContactData(jid: String, nickName: String) {
        controller.saveContactData(jid, nickName)
    }

    fun removeContact(jid: String) {
        controller.removeContact(jid)
    }
}