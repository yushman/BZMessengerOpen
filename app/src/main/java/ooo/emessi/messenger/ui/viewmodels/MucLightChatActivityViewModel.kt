package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.controllers.MUCLightChatsController
import ooo.emessi.messenger.managers.MessagesManager
import org.koin.core.KoinComponent
import org.koin.core.get

class MucLightChatActivityViewModel(private val chatId: String) : ViewModel(), KoinComponent{

    private val messageRepo: MessageRepo = get()
    private val chatRepo: ChatRepo = get()
    private val chatsController =
        MUCLightChatsController(chatId)

    val messageItems = chatsController.messageItems
    val chat = chatRepo.loadChatById(chatId)
    val messageSended: LiveData<Boolean> = chatsController.messageSended


    fun sendMessage(messageBody: String) {
        chatsController.sendMessage(messageBody)
    }

    fun deleteChat() {
        chatsController.deleteChat()
    }

    fun clearHistory() {
        chatsController.clearChatHistory()
    }

    fun flushUnread() {
        chatsController.flushUnread()
    }

    fun sendAttachments(list: List<String>) {

        chatsController.sendAttachments(list)
    }

    fun flushMessageSended() {
        chatsController.flushMessageSended()
    }

    fun deleteMessage(it: BZMessage) {
        chatsController.deleteMessage(it)
    }

    fun setCorrectedMessage(it: BZMessage) {
        chatsController.setCorrectedMessage(it)
    }

    fun setReplyedMessage(it: BZMessage) {
        chatsController.setReplyedMessage(it)
    }

    fun flushMessageActions() {
        chatsController.flushMessageActions()
    }
}