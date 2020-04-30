package ooo.emessi.messenger.controllers

import kotlinx.coroutines.CoroutineScope
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.view_item_model.ChatViewMaker
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.managers.account.AccountManager
import ooo.emessi.messenger.utils.transform
import org.koin.core.KoinComponent
import org.koin.core.get

class ChatListController : KoinComponent {

    private val chatRepo: ChatRepo = get()
    private val chatViewMaker: ChatViewMaker = get()

//    val chatViewItems = chatRepo.loadChats().transform{cookChatListViewItems(it)}

    fun getChatViewItems(viewModelScope: CoroutineScope) =
        chatRepo.loadChats().transform(viewModelScope){cookChatListViewItems(it)}

    private fun cookChatListViewItems(list: List<ChatDto>?): List<ChatViewItem> {
        var result = listOf<ChatViewItem>()
        if (list.isNullOrEmpty()) return result
        result = chatViewMaker.makeList(list)
        return result
    }

    fun logout() {
        AccountManager().logOut()
    }

}