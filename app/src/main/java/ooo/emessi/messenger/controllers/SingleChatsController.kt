package ooo.emessi.messenger.controllers

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model.wrapped_model.MessageItem
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.data.repo.ContactRepo
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.managers.*
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.impl.JidCreate
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.net.URL

class SingleChatsController(private val chatId: String) : KoinComponent {

    val messagesManager = MessagesManager(chatId)
    val chatManager = ChatManager(chatId)

    val messages = messagesManager.messages
    val messageItems: MediatorLiveData<List<MessageItem>> = MediatorLiveData()
    val messageSended: LiveData<Boolean> = messagesManager.messageSended
    val chat = chatManager.chatLD

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

    fun sendAttachments(attachments: List<String>) = CoroutineScope(Dispatchers.IO).launch  {
        messagesManager.sendAttachments(attachments, chatManager.chat)
    }

    fun createSingleChat() = CoroutineScope(Dispatchers.IO).launch{
        chatManager.createChat()
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

    private fun logProgress(uploadedBytes: Long, totalBytes: Long) {
        Log.d("UPLOAD", "" + uploadedBytes / totalBytes * 100)
    }

    fun getMimeType(path: String): String{
        val ext = path.substring(path.lastIndexOf(".")).substring(1)
        var type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        if (type == null || type.isEmpty()) type = "*/*"
        return type
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

    //    fun createMessage(messageBody: String): BZMessage {
//        var message: BZMessage? = null
//        correctedMessage?.let {
//            message = messagesManager.createCorrectedMessage(messageBody, it)
//        }
//        replyedMessage?.let {
//            message = messagesManager.createReplyMessage(messageBody, it)
//        }
//        if (message == null) message = messagesManager.createSimpleMessage(messageBody)
//        return message!!
//    }
}


