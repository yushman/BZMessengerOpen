package ooo.emessi.messenger.ui.viewmodels

import ooo.emessi.messenger.controllers.ContactPickController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

class ContactPickActivityViewModel : AbstractContactListViewModel(){
    override val contactListController = ContactPickController()
    val contactPickItems = contactListController.contactPickViewItems
    val selectedContacts = contactListController.selectedContacts

    fun handlePickContact(jid: String){
        contactListController.handlePickContact(jid)
    }

    fun handleUnpickContact(jid: String) {
        contactListController.handleUnpickContact(jid)
    }

    fun addContactToChat(
        chatDto: ChatDto
    ) {
        contactListController.addContactToChat(chatDto)
    }

    fun createNewChat(name: String) {
        contactListController.createNewChat(name)
    }

}