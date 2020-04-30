package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.view_item_model.ContactViewMaker
import ooo.emessi.messenger.data.model.view_item_model.contact.ContactViewItem
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.managers.chat.ChatFactory
import org.koin.core.KoinComponent
import org.koin.core.get

abstract class AbstractContactListController : KoinComponent{
    protected val contactRepo = get<ContactRepo>()
    protected val chatFactory = get<ChatFactory>()
    val contactViewItems: MutableLiveData<List<ContactViewItem>> = MutableLiveData()
    val newChat: MutableLiveData<ChatDto> = MutableLiveData()

    open fun loadContacts() = contactViewItems.postValue(cookChatViewItems(contactRepo.getContacts()))

    protected fun cookChatViewItems(contactDtos: List<ContactDto>): List<ContactViewItem> {
        return ContactViewMaker().makeList(contactDtos)
    }
}