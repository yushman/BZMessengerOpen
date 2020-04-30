package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ooo.emessi.messenger.controllers.ChatListController
import org.koin.core.KoinComponent

class ChatsListViewModel : ViewModel(), KoinComponent{

    private val chatListController = ChatListController()
    val chats = chatListController.getChatViewItems(viewModelScope)

    fun logout() {
        chatListController.logout()
    }
}