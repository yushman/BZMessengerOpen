package ooo.emessi.messenger.ui.viewmodels

import ooo.emessi.messenger.controllers.ContactSelectController
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto

class ContactSelectActivityViewModel : AbstractContactListViewModel() {
    override val contactListController = ContactSelectController()

    fun createNewChat(contactDto: ContactDto) = contactListController.createNewChat(contactDto)
}