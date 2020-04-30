package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactPickViewItem
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactViewItem
import ooo.emessi.messenger.managers.chat.MucChatManager

class ContactPickController : AbstractContactListController(){
    val contactPickViewItems: MutableLiveData<List<ContactPickViewItem>> = MutableLiveData()
    val selectedContacts = Transformations.map(contactPickViewItems) { list -> list.filter { it.isSelected }}

    override fun loadContacts() {
        super.loadContacts()
        contactPickViewItems.postValue(cookContactPickViewItems(contactViewItems.value))
    }

    fun handlePickContact(jid: String){
        contactPickViewItems.value = contactPickViewItems.value!!.map {
            if (it.contactViewItem.contactDto.contactJid == jid) it.copy(isSelected = !it.isSelected)
            else it
        }
    }

    fun handleUnpickContact(jid: String) {
        contactPickViewItems.value = contactPickViewItems.value!!.map {
            if (it.contactViewItem.contactDto.contactJid == jid) it.copy(isSelected = false)
            else it
        }
    }

    fun addContactToChat(
        chatDto: ChatDto
    ) {
        selectedContacts.value?.let { list ->
            val mucm = MucChatManager(chatDto)
            mucm.addOccupants(list.map { it.contactViewItem.contactDto })
        }
    }

    fun createNewChat(name: String) {
        selectedContacts.value?.let { list ->
            val chat = chatFactory.createMucChat(name, list.map { it.contactViewItem.contactDto })
            newChat.postValue(chat)
        }
    }

    private fun cookContactPickViewItems(list: List<ContactViewItem>?): List<ContactPickViewItem> {
        var result = listOf<ContactPickViewItem>()
        if (list.isNullOrEmpty()) return result
        result = list.map { ContactPickViewItem(false, it) }
        return result
    }
}