package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.controllers.SingleChatsController
import org.koin.core.KoinComponent
import org.koin.core.get

class SingleChatInfoActivityViewModel (private val chatId: String) : ViewModel(), KoinComponent{
    private val chatRepo: ChatRepo = get()

    private val chatManager =
        SingleChatsController(chatId)
    val chat = chatRepo.loadChatById(chatId)

    fun leaveChat() {
        chatManager.deleteChat()
    }
}