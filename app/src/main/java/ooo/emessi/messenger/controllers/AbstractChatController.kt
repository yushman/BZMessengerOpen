package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ooo.emessi.messenger.IOScope
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.view_item_model.ChatViewMaker
import ooo.emessi.messenger.data.model.view_item_model.MessageListItemMaker
import ooo.emessi.messenger.data.model.view_item_model.MessageViewMaker
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.data.model.wrapped_model.AttachmentMessage
import ooo.emessi.messenger.managers.chat.AbstractChatManager
import ooo.emessi.messenger.managers.chat.MessageSendListener
import ooo.emessi.messenger.utils.transform
import ooo.emessi.messenger.xmpp.XMPPApi
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AbstractChatController(private val chatDto: ChatDto) : KoinComponent, MessageSendListener {

    protected val ioScope by inject<IOScope>()
    protected val xmppApi by inject<XMPPApi>()
    protected val messageViewMaker = MessageViewMaker()
    protected val messageListMaker = MessageListItemMaker()
    protected val correctedMessage: MutableLiveData<MessageDto> = MutableLiveData()
    protected val replyedMessage: MutableLiveData<MessageDto> = MutableLiveData()
    val chatViewItem: MutableLiveData<ChatViewItem> = MutableLiveData()
    val messageSended: MutableLiveData<MessageDto> = MutableLiveData()
    val uploadResult: MutableLiveData<AttachmentMessage> = MutableLiveData()
    abstract val chatManager: AbstractChatManager

    override fun onSended(messageDto: MessageDto) {
        messageSended.postValue(messageDto)
        flushMessageActions()
    }

    override fun onUploadResult(result: Pair<AttachmentMessage, Boolean>) {
        if (!result.second) uploadResult.postValue(result.first)
    }

    fun loadChatInfo(){
        chatViewItem.postValue(cookChatItem())
    }

    fun getMessageViewItems(viewModelScope: CoroutineScope) =
        chatManager.fetchMessages().transform(viewModelScope){cookMessageItems(it)}

    //Chat Actions
    fun deleteChat() = ioScope.launch {
        chatManager.deleteChat()
    }

    fun clearChatHistory() = ioScope.launch {
        chatManager.clearChatHistory()
    }

    fun flushUnread() = ioScope.launch {
        chatManager.flushChatUnread()
    }

    //Message Basics
    fun sendMessage(messageBody: String) = ioScope.launch {
        chatManager.sendMessage(messageBody, replyedMessage.value, correctedMessage.value)
    }

    fun sendMessage(messageDto: MessageDto) = ioScope.launch {
        chatManager.sendMessage(messageDto)
    }

    fun sendAttachments(attachments: List<String>) {
        chatManager.sendAttachments(attachments, replyedMessage.value)
    }

    fun deleteMessage(message: MessageDto) = ioScope.launch {
        chatManager.deleteMessage(message)
    }

    //Message Actions

    fun flushMessageSended() {
        messageSended.value = null
    }

    fun setReplyedMessage(messageDto: MessageDto) {
        replyedMessage.value = messageDto
    }

    fun setCorrectedMessage(messageDto: MessageDto) {
        correctedMessage.value = messageDto
    }

    //Service
    fun flushMessageActions() {
        correctedMessage.value = null
        replyedMessage.value = null
    }

    private fun cookMessageItems(list: List<MessageDto>?) = if (list.isNullOrEmpty()) emptyList()
    else
        messageListMaker.makeList(messageViewMaker.makeList(list))

    private fun cookChatItem() = ChatViewMaker().make(chatDto)

}