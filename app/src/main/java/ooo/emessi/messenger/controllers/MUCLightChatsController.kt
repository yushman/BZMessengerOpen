package ooo.emessi.messenger.controllers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model.bz_model.muc_affiliation.BZMucAffiliation
import ooo.emessi.messenger.data.model.wrapped_model.MessageItem
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.managers.*
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smackx.muclight.MUCLightAffiliation
import org.jxmpp.jid.Jid
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class MUCLightChatsController (private val chatId: String): KoinComponent{

    val messagesManager = MessagesManager(chatId)
    val chatManager = MucChatManager(chatId)

    val messages: LiveData<List<BZMessage>> = messagesManager.messages
    val messageItems: MediatorLiveData<List<MessageItem>> = MediatorLiveData()
    val messageSended: LiveData<Boolean> = messagesManager.messageSended

    init {
        messageItems.addSource(messages){postMessages() }
    }

    private fun postMessages() = CoroutineScope(Dispatchers.IO).launch{
        messageItems.postValue(messagesManager.createMessageItem(messages.value))
    }

    fun sendMessage(messageBody: String) {
        val message = messagesManager.createMessage(messageBody)
        sendMessage(message)
    }

    fun sendMessage(message: BZMessage) = CoroutineScope(Dispatchers.IO).launch {
        messagesManager.sendMessage(message, chatManager.chat)
    }

    fun createMucLightChat(roomName: String, occupants: List<Jid>) = CoroutineScope(Dispatchers.IO).launch{
        chatManager.createChat(roomName, occupants)
    }

    fun deleteChat() = CoroutineScope(Dispatchers.IO).launch{
        chatManager.deleteChat()
    }

    fun clearChatHistory() = CoroutineScope(Dispatchers.IO).launch {
        messagesManager.clearMessageHistory()
    }

    fun flushUnread() = CoroutineScope(Dispatchers.IO).launch {
        chatManager.flushChatUnread()
    }

    fun updateLastActivity() = CoroutineScope(Dispatchers.IO).launch{
        chatManager.updateChatLastActivity()
    }

    fun deleteMessage(it: BZMessage) = CoroutineScope(Dispatchers.IO).launch{

        messagesManager.deleteMessage(it)
        chatManager.updateChatLastMessage()

    }

    fun flushMessageSended() {
        messagesManager.messageSended.value = false
    }

    fun setReplyedMessage(bzMessage: BZMessage) {
        messagesManager.replyedMessage = bzMessage
    }

    fun setCorrectedMessage(bzMessage: BZMessage) {
        messagesManager.correctedMessage = bzMessage
    }

    fun flushMessageActions() {
        messagesManager.correctedMessage = null
        messagesManager.replyedMessage = null
    }

    fun addOccupants(occupants: List<Jid>) = CoroutineScope(Dispatchers.IO).launch{
        chatManager.addOccupants(occupants)
    }

    fun sendAttachments(list: List<String>) = CoroutineScope(Dispatchers.IO).launch {
        messagesManager.sendAttachments(list, chatManager.chat)
    }
}