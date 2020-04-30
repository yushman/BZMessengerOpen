package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ooo.emessi.messenger.controllers.AbstractChatController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto

abstract class AbstractChatViewModel (private val chatDto: ChatDto): ViewModel(){
    abstract val controller: AbstractChatController
    val messageItems = controller.getMessageViewItems(viewModelScope)
    val messageSended = controller.messageSended
    val uploadResult = controller.uploadResult
    val chatViewItem = controller.chatViewItem

    fun loadChatInfo() {
        controller.loadChatInfo()
    }

    fun sendMessage(messageBody: String = "") {
        controller.sendMessage(messageBody)
    }

    fun sendAttachments(attachments: List<String> = listOf()){
        controller.sendAttachments(attachments)
    }

    fun deleteChat() {
        controller.deleteChat()
    }

    fun clearChatHistory() {
        controller.clearChatHistory()
    }

    fun flushUnread() {
        controller.flushUnread()
    }

    fun flushMessageSended() {
        controller.flushMessageSended()
    }

    fun deleteMessage(it: MessageDto) {
        controller.deleteMessage(it)
    }

    fun setReplyedMessage(it: MessageDto) {
        controller.setReplyedMessage(it)
    }

    fun setCorrectedMessage(it: MessageDto) {
        controller.setCorrectedMessage(it)
    }

    fun flushMessageActions() {
        controller.flushMessageActions()
    }
}