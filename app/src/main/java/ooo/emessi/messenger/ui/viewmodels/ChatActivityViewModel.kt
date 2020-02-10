package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.controllers.SingleChatsController
import org.koin.core.KoinComponent
import org.koin.core.get

class ChatActivityViewModel(private val chatId: String): ViewModel(), KoinComponent{

    private val messageRepo: MessageRepo = get()
    private val chatsRepo: ChatRepo = get()

    private val chatsController = SingleChatsController(chatId)

    val messageSended: LiveData<Boolean> = chatsController.messageSended
    val messageItems = chatsController.messageItems
    val chat = chatsController.chat

//    val attachments: LiveData<List<ABZAttachment>> = attachmentManager.attachments

    fun sendMessage(messageBody: String = "", attachments: List<String> = listOf()) {
        if (attachments.isNotEmpty()) chatsController.sendAttachments(attachments)
        else chatsController.sendMessage(messageBody)
    }

    fun deleteChat() {
        chatsController.deleteChat()
    }

    fun clearChatHistory() {
        chatsController.clearChatHistory()
    }

    fun createChat(chatId: String) {
        chatsController.createSingleChat()
    }

    fun flushUnread() {
        chatsController.flushUnread()
    }

    fun flushMessageSended() {
        chatsController.flushMessageSended()
    }

    fun updateLastActivity() {
        chatsController.updateLastActivity()

    }

    fun deleteMessage(it: BZMessage) {
        chatsController.deleteMessage(it)
    }

    fun setReplyedMessage(it: BZMessage) {
        chatsController.setReplyedMessage(it)
    }

    fun setCorrectedMessage(it: BZMessage) {
        chatsController.setCorrectedMessage(it)
    }

    fun flushMessageActions() {
        chatsController.flushMessageActions()
    }

}