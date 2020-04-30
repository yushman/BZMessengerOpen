package ooo.emessi.messenger.controllers

import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto

class ContactSelectController : AbstractContactListController(){

    fun createNewChat(contactDto: ContactDto) {
        val chat = chatFactory.getChatFromContact(contactDto)
        newChat.postValue(chat)
    }
}