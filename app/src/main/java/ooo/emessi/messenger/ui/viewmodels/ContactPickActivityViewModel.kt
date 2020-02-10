package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.wrapped_model.ContactPickItem
import ooo.emessi.messenger.controllers.ContactPickController
import org.koin.core.KoinComponent

class ContactPickActivityViewModel : ViewModel(), KoinComponent{
    private val contactPickManager =
        ContactPickController()

    var contacts = contactPickManager.contactsW
    var selectedContacts = contactPickManager.selectedContact

    fun handlePickContact(jid: String){
        contactPickManager.handlePickContact(jid)
    }

    fun handleUnpickContact(jid: String) {
        contactPickManager.handleUnpickContact(jid)
    }

    fun addContactToChat(
        chatId: String,
        selectedList: List<ContactPickItem>
    ) {
        contactPickManager.addContactToChat(chatId, selectedList)
    }

    fun createNewChat(name: String) {
        contactPickManager.createNewChat(name)
    }

    fun loadContacts() {
        contactPickManager.loadContacts()
    }

}